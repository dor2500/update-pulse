package com.dor2500.uptrack

import android.app.Application
import com.dor2500.uptrack.di.AppContainer
import com.dor2500.uptrack.notifications.NotificationHelper
import com.dor2500.uptrack.worker.WorkScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class UpTrackApp : Application() {
    lateinit var container: AppContainer
        private set

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        NotificationHelper.createChannels(this)

        appScope.launch {
            container.repository.ensureSeeded()
            val prefs = container.preferencesRepository.preferencesFlow.first()
            WorkScheduler.schedule(this@UpTrackApp, prefs.pollInterval, prefs.wifiOnly)
        }
    }
}
