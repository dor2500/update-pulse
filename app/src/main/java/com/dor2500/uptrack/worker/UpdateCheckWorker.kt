package com.dor2500.uptrack.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dor2500.uptrack.UpTrackApp

class UpdateCheckWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val app = applicationContext as UpTrackApp
        return try {
            app.container.repository.checkAllSubscribed(notify = true)
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}
