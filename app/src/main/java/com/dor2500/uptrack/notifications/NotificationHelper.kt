package com.dor2500.uptrack.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.dor2500.uptrack.MainActivity
import com.dor2500.uptrack.R
import com.dor2500.uptrack.data.local.UpdateEventEntity

object NotificationHelper {
    const val CHANNEL_ID = "service_updates"
    private const val GROUP_KEY = "com.dor2500.uptrack.UPDATE_GROUP"
    private const val SUMMARY_ID = 9001

    fun createChannels(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notif_channel_updates_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.notif_channel_updates_desc)
        }
        manager.createNotificationChannel(channel)
    }

    fun showUpdateNotification(context: Context, event: UpdateEventEntity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }

        createChannels(context)

        val notifId = event.id.toInt().coerceAtLeast(1)

        val contentIntent = PendingIntent.getActivity(
            context,
            notifId,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val openUrlIntent = PendingIntent.getActivity(
            context,
            notifId + 10000,
            Intent(Intent.ACTION_VIEW, Uri.parse(event.releaseUrl)),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val markReadIntent = PendingIntent.getBroadcast(
            context,
            notifId + 20000,
            Intent(context, NotificationActionReceiver::class.java).apply {
                action = NotificationActionReceiver.ACTION_MARK_READ
                putExtra(NotificationActionReceiver.EXTRA_EVENT_ID, event.id)
                putExtra(NotificationActionReceiver.EXTRA_NOTIF_ID, notifId)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = context.getString(R.string.notif_new_version, event.serviceName, event.version)
        val snippet = event.changelog.ifBlank { event.releaseTitle }.take(240)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_uptrack)
            .setContentTitle(title)
            .setContentText(snippet)
            .setStyle(NotificationCompat.BigTextStyle().bigText(snippet))
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .setGroup(GROUP_KEY)
            .addAction(0, context.getString(R.string.notif_action_open), openUrlIntent)
            .addAction(0, context.getString(R.string.notif_action_mark_read), markReadIntent)
            .build()

        val summary = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_uptrack)
            .setContentTitle(context.getString(R.string.app_name))
            .setGroup(GROUP_KEY)
            .setGroupSummary(true)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(notifId, notification)
            notify(SUMMARY_ID, summary)
        }
    }
}
