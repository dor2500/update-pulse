package com.dor2500.updatepulse.data.repository

import android.content.Context
import com.dor2500.updatepulse.data.local.AppDatabase
import com.dor2500.updatepulse.data.local.CuratedCatalog
import com.dor2500.updatepulse.data.local.TrackedServiceEntity
import com.dor2500.updatepulse.data.local.UpdateEventEntity
import com.dor2500.updatepulse.data.network.FetchedRelease
import com.dor2500.updatepulse.data.network.SourceFetcher
import com.dor2500.updatepulse.notifications.NotificationHelper
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

data class CheckSummary(
    val checkedCount: Int,
    val newUpdatesCount: Int
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
    private val fetcher: SourceFetcher
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
    }

    suspend fun setSubscribed(id: String, subscribed: Boolean) {
        serviceDao.setSubscribed(id, subscribed)
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
        return fetcher.fetchLatest(service)
    }

    suspend fun checkAllSubscribed(notify: Boolean): CheckSummary = coroutineScope {
        ensureSeeded()
        val subscribed = serviceDao.getSubscribed()
        if (subscribed.isEmpty()) return@coroutineScope CheckSummary(0, 0)

        val semaphore = Semaphore(4)
        val results = subscribed.map { service ->
            async {
                semaphore.withPermit {
                    checkSingleService(service, notify)
                }
            }
        }.awaitAll()

        val newCount = results.count { it }
        CheckSummary(checkedCount = subscribed.size, newUpdatesCount = newCount)
    }

    private suspend fun checkSingleService(
        service: TrackedServiceEntity,
        notify: Boolean
    ): Boolean {
        val now = System.currentTimeMillis()
        val result = fetcher.fetchLatest(service)
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
                    if (notify && service.lastKnownVersion != null) {
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
