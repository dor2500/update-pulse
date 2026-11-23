package com.dor2500.uptrack.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dor2500.uptrack.data.model.AppLanguage
import com.dor2500.uptrack.data.model.PollInterval
import com.dor2500.uptrack.data.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.DARK,
    val dynamicColor: Boolean = true,
    val pollInterval: PollInterval = PollInterval.HOUR_3,
    val wifiOnly: Boolean = false,
    val autoCheckAppUpdate: Boolean = true,
    val autoScanApps: Boolean = true,
    val hasCompletedInitialScan: Boolean = false,
    val githubToken: String = "",
    val appLanguage: AppLanguage = AppLanguage.SYSTEM
)

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val POLL_INTERVAL = stringPreferencesKey("poll_interval")
        val WIFI_ONLY = booleanPreferencesKey("wifi_only")
        val AUTO_CHECK_APP_UPDATE = booleanPreferencesKey("auto_check_app_update")
        val AUTO_SCAN_APPS = booleanPreferencesKey("auto_scan_apps")
        val HAS_COMPLETED_INITIAL_SCAN = booleanPreferencesKey("has_completed_initial_scan")
        val GITHUB_TOKEN = stringPreferencesKey("github_token")
        val APP_LANGUAGE = stringPreferencesKey("app_language")
    }

    val preferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        val theme = prefs[Keys.THEME_MODE]?.let {
            runCatching { ThemeMode.valueOf(it) }.getOrNull()
        } ?: ThemeMode.DARK

        val interval = prefs[Keys.POLL_INTERVAL]?.let {
            runCatching { PollInterval.valueOf(it) }.getOrNull()
        } ?: PollInterval.HOUR_3

        val language = prefs[Keys.APP_LANGUAGE]?.let {
            runCatching { AppLanguage.valueOf(it) }.getOrNull()
        } ?: AppLanguage.SYSTEM

        UserPreferences(
            themeMode = theme,
            dynamicColor = prefs[Keys.DYNAMIC_COLOR] ?: true,
            pollInterval = interval,
            wifiOnly = prefs[Keys.WIFI_ONLY] ?: false,
            autoCheckAppUpdate = prefs[Keys.AUTO_CHECK_APP_UPDATE] ?: true,
            autoScanApps = prefs[Keys.AUTO_SCAN_APPS] ?: true,
            hasCompletedInitialScan = prefs[Keys.HAS_COMPLETED_INITIAL_SCAN] ?: false,
            githubToken = prefs[Keys.GITHUB_TOKEN] ?: "",
            appLanguage = language
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DYNAMIC_COLOR] = enabled }
    }

    suspend fun setPollInterval(interval: PollInterval) {
        context.dataStore.edit { it[Keys.POLL_INTERVAL] = interval.name }
    }

    suspend fun setWifiOnly(wifiOnly: Boolean) {
        context.dataStore.edit { it[Keys.WIFI_ONLY] = wifiOnly }
    }

    suspend fun setAutoCheckAppUpdate(enabled: Boolean) {
        context.dataStore.edit { it[Keys.AUTO_CHECK_APP_UPDATE] = enabled }
    }

    suspend fun setAutoScanApps(enabled: Boolean) {
        context.dataStore.edit { it[Keys.AUTO_SCAN_APPS] = enabled }
    }

    suspend fun setInitialScanCompleted() {
        context.dataStore.edit { it[Keys.HAS_COMPLETED_INITIAL_SCAN] = true }
    }

    suspend fun setGithubToken(token: String) {
        context.dataStore.edit { it[Keys.GITHUB_TOKEN] = token.trim() }
    }

    suspend fun setAppLanguage(language: AppLanguage) {
        context.dataStore.edit { it[Keys.APP_LANGUAGE] = language.name }
    }
}
