package com.dor2500.updatepulse.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dor2500.updatepulse.UpdatePulseApp

class UpdateCheckWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val app = applicationContext as UpdatePulseApp
        return try {
            app.container.repository.checkAllSubscribed(notify = true)
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}
