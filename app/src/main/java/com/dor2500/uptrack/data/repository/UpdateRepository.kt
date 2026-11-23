package com.dor2500.uptrack.data.repository

import android.content.Context
import com.dor2500.uptrack.data.local.AppDatabase
import com.dor2500.uptrack.data.local.CuratedCatalog
import com.dor2500.uptrack.data.local.InstalledAppScanner
import com.dor2500.uptrack.data.local.TrackedServiceEntity
import com.dor2500.uptrack.data.local.UpdateEventEntity
import com.dor2500.uptrack.data.network.FetchedRelease
import com.dor2500.uptrack.data.network.SourceFetcher
import com.dor2500.uptrack.data.preferences.UserPreferencesRepository
import com.dor2500.uptrack.notifications.NotificationHelper
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

data class CheckSummary(
    val checkedCount: Int,
    val newUpdatesCount: Int
)

data class AppScanSummary(
    val totalMatched: Int,
    val newlyEnabled: Int
)

@Serializable
data class BackupPayload(
    val version: Int = 1,
    val exportedAt: Long = System.currentTimeMillis(),
    val subscribedIds: List<String> = emptyList(),
    val customServices: List<TrackedServiceEntity> = emptyList()
)

class UpdateRepository(
    private val context: Context,
    private val db: AppDatabase,
    private val fetcher: SourceFetcher,
    private val prefsRepo: UserPreferencesRepository
) {
    private val serviceDao = db.trackedServiceDao()
    private val eventDao = db.updateEventDao()
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    fun observeServices(): Flow<List<TrackedServiceEntity>> = serviceDao.observeAll()
    fun observeCustomServices(): Flow<List<TrackedServiceEntity>> = serviceDao.observeCustom()
    fun observeFeed(): Flow<List<UpdateEventEntity>> = eventDao.observeAll()
    fun observeUnreadCount(): Flow<Int> = eventDao.observeUnreadCount()

    suspend fun ensureSeeded() {
        serviceDao.insertIgnoreAll(CuratedCatalog.defaultServices)
        val googleIds = listOf("google_android_beta", "google_app")
        val browserIds = listOf(
            "browser_chrome_beta",
            "browser_chrome_canary",
            "browser_firefox_nightly",
            "browser_firefox_beta",
            "browser_vivaldi",
            "browser_edge_canary"
        )
        serviceDao.updateCuratedCategory(googleIds, com.dor2500.uptrack.data.model.ServiceCategory.GOOGLE_PIXEL)
        serviceDao.updateCuratedCategory(browserIds, com.dor2500.uptrack.data.model.ServiceCategory.BROWSERS)
        serviceDao.updateCuratedCategory(
            listOf("dev_win11_insider"),
            com.dor2500.uptrack.data.model.ServiceCategory.WINDOWS_OS
        )
    }

    suspend fun scanAndAutoSubscribeInstalledApps(): AppScanSummary {
        ensureSeeded()
        val allServices = serviceDao.getAll()
        val matches = InstalledAppScanner(context).scan(allServices)
        if (matches.isEmpty()) {
            return AppScanSummary(totalMatched = 0, newlyEnabled = 0)
        }
        val byId = allServices.associateBy { it.id }
        val newlyEnabled = matches.count { match ->
            val current = byId[match.serviceId]
            current != null && (!current.isSubscribed || !current.notificationsEnabled)
        }
        matches.map { it.serviceId }.chunked(500).forEach { chunk ->
            serviceDao.enableTrackingAndNotifications(chunk)
        }
        return AppScanSummary(totalMatched = matches.size, newlyEnabled = newlyEnabled)
    }

    suspend fun setSubscribed(id: String, subscribed: Boolean) {
        serviceDao.setSubscribed(id, subscribed)
    }

    suspend fun setNotificationsEnabled(id: String, enabled: Boolean) {
        serviceDao.setNotificationsEnabled(id, enabled)
    }

    suspend fun clearFeed() {
        eventDao.deleteAll()
    }

    suspend fun checkSingleServiceById(id: String): Result<String> {
        val service = serviceDao.getById(id)
            ?: return Result.failure(IllegalStateException("Service not found"))
        val token = prefsRepo.preferencesFlow.first().githubToken
        val now = System.currentTimeMillis()
        val result = fetcher.fetchLatest(service, token)
        return result.map { release ->
            val isNew = service.lastKnownVersion != release.version
            serviceDao.update(
                service.copy(
                    lastKnownVersion = release.version,
                    lastCheckedAt = now,
                    lastError = null
                )
            )
            if (isNew) {
                eventDao.insert(
                    UpdateEventEntity(
                        serviceId = service.id,
                        serviceName = service.name,
                        category = service.category,
                        version = release.version,
                        releaseTitle = release.title,
                        changelog = release.changelog,
                        releaseUrl = release.url,
                        detectedAt = now,
                        isRead = false,
                        accentColorHex = service.accentColorHex
                    )
                )
            }
            release.version
        }.onFailure { err ->
            serviceDao.update(
                service.copy(
                    lastCheckedAt = now,
                    lastError = err.message ?: "Unknown error"
                )
            )
        }
    }

    suspend fun saveCustomService(service: TrackedServiceEntity) {
        serviceDao.upsert(service.copy(isCustom = true))
    }

    suspend fun deleteCustomService(id: String) {
        serviceDao.deleteById(id)
        eventDao.deleteForService(id)
    }

    suspend fun markEventRead(eventId: Long) {
        eventDao.markRead(eventId)
    }

    suspend fun markAllRead() {
        eventDao.markAllRead()
    }

    suspend fun testSource(service: TrackedServiceEntity): Result<FetchedRelease> {
        val token = prefsRepo.preferencesFlow.first().githubToken
        return fetcher.fetchLatest(service, token)
    }

    suspend fun checkAllSubscribed(notify: Boolean): CheckSummary = coroutineScope {
        ensureSeeded()
        val subscribed = serviceDao.getSubscribed()
        if (subscribed.isEmpty()) return@coroutineScope CheckSummary(0, 0)

        val token = prefsRepo.preferencesFlow.first().githubToken
        val semaphore = Semaphore(4)
        val results = subscribed.map { service ->
            async {
                semaphore.withPermit {
                    checkSingleService(service, notify, token)
                }
            }
        }.awaitAll()

        val newCount = results.count { it }
        CheckSummary(checkedCount = subscribed.size, newUpdatesCount = newCount)
    }

    private suspend fun checkSingleService(
        service: TrackedServiceEntity,
        notify: Boolean,
        githubToken: String
    ): Boolean {
        val now = System.currentTimeMillis()
        val result = fetcher.fetchLatest(service, githubToken)
        return result.fold(
            onSuccess = { release ->
                val isNewVersion = service.lastKnownVersion != release.version
                serviceDao.update(
                    service.copy(
                        lastKnownVersion = release.version,
                        lastCheckedAt = now,
                        lastError = null
                    )
                )
                if (isNewVersion) {
                    val event = UpdateEventEntity(
                        serviceId = service.id,
                        serviceName = service.name,
                        category = service.category,
                        version = release.version,
                        releaseTitle = release.title,
                        changelog = release.changelog,
                        releaseUrl = release.url,
                        detectedAt = now,
                        isRead = false,
                        accentColorHex = service.accentColorHex
                    )
                    val insertedId = eventDao.insert(event)
                    if (notify && service.notificationsEnabled && service.lastKnownVersion != null) {
                        NotificationHelper.showUpdateNotification(
                            context,
                            event.copy(id = insertedId)
                        )
                    }
                    true
                } else {
                    false
                }
            },
            onFailure = { error ->
                serviceDao.update(
                    service.copy(
                        lastCheckedAt = now,
                        lastError = error.message ?: "Unknown error"
                    )
                )
                false
            }
        )
    }

    suspend fun exportBackupJson(): Pair<String, Int> {
        val all = serviceDao.getAll()
        val subscribedIds = all.filter { it.isSubscribed && !it.isCustom }.map { it.id }
        val customServices = all.filter { it.isCustom }
        val payload = BackupPayload(
            subscribedIds = subscribedIds,
            customServices = customServices
        )
        return json.encodeToString(payload) to (subscribedIds.size + customServices.size)
    }

    suspend fun importBackupJson(rawJson: String): Int {
        ensureSeeded()
        val payload = json.decodeFromString<BackupPayload>(rawJson)
        var count = 0
        payload.subscribedIds.forEach { id ->
            if (serviceDao.getById(id) != null) {
                serviceDao.setSubscribed(id, true)
                count++
            }
        }
        payload.customServices.forEach { custom ->
            serviceDao.upsert(custom.copy(isCustom = true))
            count++
        }
        return count
    }
}
