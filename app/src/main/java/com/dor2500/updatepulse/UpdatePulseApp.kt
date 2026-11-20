package com.dor2500.updatepulse

import android.app.Application
import com.dor2500.updatepulse.di.AppContainer
import com.dor2500.updatepulse.notifications.NotificationHelper
import com.dor2500.updatepulse.worker.WorkScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class UpdatePulseApp : Application() {
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
            WorkScheduler.schedule(this@UpdatePulseApp, prefs.pollInterval, prefs.wifiOnly)
        }
    }
}
