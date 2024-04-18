package com.github.kimhyunjin.mywindowmanager.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.github.kimhyunjin.mywindowmanager.R


class MyForegroundService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
        return START_STICKY
    }

    private fun createNotification(): Notification {
        // Create a notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "My Foreground Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }

        val builder =
            NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("My Foreground Service")
                .setContentText("Running...").setSmallIcon(
                    R.drawable.ic_launcher_foreground
                )

        return builder.build()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    companion object {
        const val NOTIFICATION_ID = 123
        const val CHANNEL_ID = "my_foreground_service"
    }
}