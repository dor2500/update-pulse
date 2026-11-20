package com.dor2500.updatepulse.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dor2500.updatepulse.data.model.PollInterval
import com.dor2500.updatepulse.data.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.DARK,
    val dynamicColor: Boolean = true,
    val pollInterval: PollInterval = PollInterval.HOUR_3,
    val wifiOnly: Boolean = false,
    val autoCheckAppUpdate: Boolean = true
)

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val POLL_INTERVAL = stringPreferencesKey("poll_interval")
        val WIFI_ONLY = booleanPreferencesKey("wifi_only")
        val AUTO_CHECK_APP_UPDATE = booleanPreferencesKey("auto_check_app_update")
    }

    val preferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        val theme = prefs[Keys.THEME_MODE]?.let {
            runCatching { ThemeMode.valueOf(it) }.getOrNull()
        } ?: ThemeMode.DARK

        val interval = prefs[Keys.POLL_INTERVAL]?.let {
            runCatching { PollInterval.valueOf(it) }.getOrNull()
        } ?: PollInterval.HOUR_3

        UserPreferences(
            themeMode = theme,
            dynamicColor = prefs[Keys.DYNAMIC_COLOR] ?: true,
            pollInterval = interval,
            wifiOnly = prefs[Keys.WIFI_ONLY] ?: false,
            autoCheckAppUpdate = prefs[Keys.AUTO_CHECK_APP_UPDATE] ?: true
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
}
