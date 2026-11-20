package com.dor2500.updatepulse.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.dor2500.updatepulse.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_MARK_READ) {
            val eventId = intent.getLongExtra(EXTRA_EVENT_ID, -1L)
            val notifId = intent.getIntExtra(EXTRA_NOTIF_ID, -1)
            if (notifId != -1) {
                NotificationManagerCompat.from(context).cancel(notifId)
            }
            if (eventId != -1L) {
                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        AppDatabase.getInstance(context).updateEventDao().markRead(eventId)
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
        }
    }

    companion object {
        const val ACTION_MARK_READ = "com.dor2500.updatepulse.ACTION_MARK_READ"
        const val EXTRA_EVENT_ID = "extra_event_id"
        const val EXTRA_NOTIF_ID = "extra_notif_id"
    }
}
