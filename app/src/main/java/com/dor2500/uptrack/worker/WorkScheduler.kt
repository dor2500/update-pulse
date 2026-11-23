package com.dor2500.uptrack.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.dor2500.uptrack.data.model.PollInterval
import java.util.concurrent.TimeUnit

object WorkScheduler {
    private const val WORK_NAME = "uptrack_periodic_check"

    fun schedule(context: Context, interval: PollInterval, wifiOnly: Boolean) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(
                if (wifiOnly) NetworkType.UNMETERED else NetworkType.CONNECTED
            )
            .build()

        val request = PeriodicWorkRequestBuilder<UpdateCheckWorker>(
            interval.minutes,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}
