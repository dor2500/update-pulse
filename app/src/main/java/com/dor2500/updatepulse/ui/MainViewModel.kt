package com.dor2500.updatepulse.ui

import android.app.Application
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dor2500.updatepulse.UpdatePulseApp
import com.dor2500.updatepulse.data.local.TrackedServiceEntity
import com.dor2500.updatepulse.data.local.UpdateEventEntity
import com.dor2500.updatepulse.data.model.AppLanguage
import com.dor2500.updatepulse.data.model.PollInterval
import com.dor2500.updatepulse.data.model.ServiceCategory
import com.dor2500.updatepulse.data.model.ThemeMode
import com.dor2500.updatepulse.data.network.FetchedRelease
import com.dor2500.updatepulse.data.preferences.UserPreferences
import com.dor2500.updatepulse.notifications.NotificationHelper
import com.dor2500.updatepulse.updater.AppReleaseInfo
import com.dor2500.updatepulse.updater.DownloadState
import com.dor2500.updatepulse.worker.WorkScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val container = (application as UpdatePulseApp).container
    private val repository = container.repository
    private val prefsRepo = container.preferencesRepository
    private val appUpdater = container.appUpdater

    val services: StateFlow<List<TrackedServiceEntity>> = repository.observeServices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customServices: StateFlow<List<TrackedServiceEntity>> = repository.observeCustomServices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val feedEvents: StateFlow<List<UpdateEventEntity>> = repository.observeFeed()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadCount: StateFlow<Int> = repository.observeUnreadCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val preferences: StateFlow<UserPreferences> = prefsRepo.preferencesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferences())

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _snackbarEvents = MutableSharedFlow<String>()
    val snackbarEvents = _snackbarEvents.asSharedFlow()

    private val _availableAppUpdate = MutableStateFlow<AppReleaseInfo?>(null)
    val availableAppUpdate: StateFlow<AppReleaseInfo?> = _availableAppUpdate.asStateFlow()

    private val _isCheckingAppUpdate = MutableStateFlow(false)
    val isCheckingAppUpdate: StateFlow<Boolean> = _isCheckingAppUpdate.asStateFlow()

    private val _isScanningApps = MutableStateFlow(false)
    val isScanningApps: StateFlow<Boolean> = _isScanningApps.asStateFlow()

    private val _downloadState = MutableStateFlow<DownloadState?>(null)
    val downloadState: StateFlow<DownloadState?> = _downloadState.asStateFlow()

    init {
        viewModelScope.launch {
            val prefs = prefsRepo.preferencesFlow.first()
            if (prefs.autoScanApps || !prefs.hasCompletedInitialScan) {
                scanInstalledApps(silentIfNoNew = true)
            }
            if (prefs.autoCheckAppUpdate) {
                checkForAppUpdate(silentIfUpToDate = true)
            }
        }
    }

    fun checkNow() {
        if (_isRefreshing.value) return
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val summary = repository.checkAllSubscribed(notify = false)
                _snackbarEvents.emit(
                    "Checked ${summary.checkedCount} services • ${summary.newUpdatesCount} new updates"
                )
            } catch (e: Exception) {
                _snackbarEvents.emit("Check failed: ${e.message ?: "Unknown error"}")
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun toggleSubscription(service: TrackedServiceEntity) {
        viewModelScope.launch {
            repository.setSubscribed(service.id, !service.isSubscribed)
        }
    }

    fun toggleServiceNotifications(service: TrackedServiceEntity) {
        viewModelScope.launch {
            repository.setNotificationsEnabled(service.id, !service.notificationsEnabled)
        }
    }

    fun checkSingleService(service: TrackedServiceEntity) {
        viewModelScope.launch {
            val res = repository.checkSingleServiceById(service.id)
            res.fold(
                onSuccess = { ver -> _snackbarEvents.emit("${service.name}: $ver") },
                onFailure = { err -> _snackbarEvents.emit("${service.name}: ${err.message}") }
            )
        }
    }

    fun clearFeed() {
        viewModelScope.launch {
            repository.clearFeed()
            _snackbarEvents.emit("Update history cleared")
        }
    }

    fun markEventRead(eventId: Long) {
        viewModelScope.launch {
            repository.markEventRead(eventId)
        }
    }

    fun markAllRead() {
        viewModelScope.launch {
            repository.markAllRead()
        }
    }

    fun saveCustomService(service: TrackedServiceEntity) {
        viewModelScope.launch {
            repository.saveCustomService(service)
        }
    }

    fun deleteCustomService(id: String) {
        viewModelScope.launch {
            repository.deleteCustomService(id)
        }
    }

    suspend fun testSource(service: TrackedServiceEntity): Result<FetchedRelease> {
        return repository.testSource(service)
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { prefsRepo.setThemeMode(mode) }
    }

    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch { prefsRepo.setDynamicColor(enabled) }
    }

    fun setPollInterval(interval: PollInterval) {
        viewModelScope.launch {
            prefsRepo.setPollInterval(interval)
            WorkScheduler.schedule(getApplication(), interval, preferences.value.wifiOnly)
        }
    }

    fun setWifiOnly(wifiOnly: Boolean) {
        viewModelScope.launch {
            prefsRepo.setWifiOnly(wifiOnly)
            WorkScheduler.schedule(getApplication(), preferences.value.pollInterval, wifiOnly)
        }
    }

    fun setAutoCheckAppUpdate(enabled: Boolean) {
        viewModelScope.launch { prefsRepo.setAutoCheckAppUpdate(enabled) }
    }

    fun setAutoScanApps(enabled: Boolean) {
        viewModelScope.launch { prefsRepo.setAutoScanApps(enabled) }
    }

    fun scanInstalledApps(silentIfNoNew: Boolean = false) {
        if (_isScanningApps.value) return
        viewModelScope.launch {
            _isScanningApps.value = true
            try {
                val summary = repository.scanAndAutoSubscribeInstalledApps()
                val prefs = prefsRepo.preferencesFlow.first()
                prefsRepo.setInitialScanCompleted()
                WorkScheduler.schedule(getApplication(), prefs.pollInterval, prefs.wifiOnly)
                if (!silentIfNoNew || summary.newlyEnabled > 0) {
                    val app = getApplication<Application>()
                    _snackbarEvents.emit(
                        app.getString(
                            com.dor2500.updatepulse.R.string.scan_apps_result,
                            summary.totalMatched,
                            summary.newlyEnabled
                        )
                    )
                }
            } catch (e: Exception) {
                if (!silentIfNoNew) {
                    _snackbarEvents.emit("Scan failed: ${e.message ?: "Unknown error"}")
                }
            } finally {
                _isScanningApps.value = false
            }
        }
    }

    fun setGithubToken(token: String) {
        viewModelScope.launch {
            prefsRepo.setGithubToken(token)
            _snackbarEvents.emit("GitHub token saved")
        }
    }

    fun setAppLanguage(language: AppLanguage) {
        viewModelScope.launch {
            prefsRepo.setAppLanguage(language)
            val locales = if (language.tag.isEmpty()) {
                LocaleListCompat.getEmptyLocaleList()
            } else {
                LocaleListCompat.forLanguageTags(language.tag)
            }
            AppCompatDelegate.setApplicationLocales(locales)
        }
    }

    fun sendTestNotification() {
        val sample = UpdateEventEntity(
            id = 99999L,
            serviceId = "test",
            serviceName = "UpdatePulse Test",
            category = ServiceCategory.CUSTOM,
            version = "v1.3.0",
            releaseTitle = "Test Notification",
            changelog = "Background notifications are working properly!",
            releaseUrl = "https://github.com/dor2500/update-pulse/releases",
            detectedAt = System.currentTimeMillis()
        )
        NotificationHelper.showUpdateNotification(getApplication(), sample)
    }

    fun checkForAppUpdate(silentIfUpToDate: Boolean = false) {
        if (_isCheckingAppUpdate.value) return
        viewModelScope.launch {
            _isCheckingAppUpdate.value = true
            val result = appUpdater.checkForUpdate()
            _isCheckingAppUpdate.value = false
            result.fold(
                onSuccess = { release ->
                    if (release != null) {
                        _availableAppUpdate.value = release
                    } else if (!silentIfUpToDate) {
                        _snackbarEvents.emit("UpdatePulse is up to date")
                    }
                },
                onFailure = { err ->
                    if (!silentIfUpToDate) {
                        _snackbarEvents.emit("Update check failed: ${err.message}")
                    }
                }
            )
        }
    }

    fun dismissAppUpdateDialog() {
        _availableAppUpdate.value = null
        _downloadState.value = null
    }

    fun downloadAndInstallAppUpdate(release: AppReleaseInfo) {
        viewModelScope.launch {
            appUpdater.downloadApk(release.apkDownloadUrl).collect { state ->
                _downloadState.value = state
                if (state is DownloadState.Ready) {
                    appUpdater.installApk(state.apkFile)
                }
            }
        }
    }

    fun exportBackup(uri: Uri) {
        viewModelScope.launch {
            try {
                val (json, count) = repository.exportBackupJson()
                withContext(Dispatchers.IO) {
                    getApplication<Application>().contentResolver.openOutputStream(uri)?.use {
                        it.write(json.toByteArray(Charsets.UTF_8))
                    }
                }
                _snackbarEvents.emit("Exported $count services")
            } catch (e: Exception) {
                _snackbarEvents.emit("Backup error: ${e.message}")
            }
        }
    }

    fun importBackup(uri: Uri) {
        viewModelScope.launch {
            try {
                val raw = withContext(Dispatchers.IO) {
                    getApplication<Application>().contentResolver.openInputStream(uri)?.use {
                        it.bufferedReader(Charsets.UTF_8).readText()
                    } ?: ""
                }
                val count = repository.importBackupJson(raw)
                _snackbarEvents.emit("Imported $count services")
            } catch (e: Exception) {
                _snackbarEvents.emit("Backup error: ${e.message}")
            }
        }
    }
}
