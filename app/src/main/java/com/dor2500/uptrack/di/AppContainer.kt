package com.dor2500.uptrack.di

import android.content.Context
import com.dor2500.uptrack.data.local.AppDatabase
import com.dor2500.uptrack.data.network.SourceFetcher
import com.dor2500.uptrack.data.preferences.UserPreferencesRepository
import com.dor2500.uptrack.data.repository.UpdateRepository
import com.dor2500.uptrack.updater.AppUpdater

class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    val database: AppDatabase by lazy { AppDatabase.getInstance(appContext) }
    val sourceFetcher: SourceFetcher by lazy { SourceFetcher() }
    val preferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(appContext)
    }
    val repository: UpdateRepository by lazy {
        UpdateRepository(appContext, database, sourceFetcher, preferencesRepository)
    }
    val appUpdater: AppUpdater by lazy { AppUpdater(appContext) }
}
