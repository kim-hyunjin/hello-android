package com.github.kimhyunjin.musicplayer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.drawable.Icon
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class MediaPlayerService : Service() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        Log.i("MediaPlayerService", "onCreate!!")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    MEDIA_CHANNEL_ID,
                    "MEDIA_PLAYER",
                    NotificationManager.IMPORTANCE_LOW
                )
            )
        }

        val mainPendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val playIcon = Icon.createWithResource(this, R.drawable.baseline_play_arrow_24)
        val pauseIcon = Icon.createWithResource(this, R.drawable.baseline_pause_24)
        val stopIcon = Icon.createWithResource(this, R.drawable.baseline_stop_24)

        val pausePendingIntent = PendingIntent.getService(
            this,
            0,
            Intent(this, MediaPlayerService::class.java).apply {
                action = MEDIA_PLAYER_PAUSE
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val playPendingIntent = PendingIntent.getService(
            this,
            0,
            Intent(this, MediaPlayerService::class.java).apply {
                action = MEDIA_PLAYER_PLAY
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val stopPendingIntent = PendingIntent.getService(
            this,
            0,
            Intent(this, MediaPlayerService::class.java).apply {
                action = MEDIA_PLAYER_STOP
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, MEDIA_CHANNEL_ID).apply {
                setSmallIcon(R.drawable.baseline_stars_24)
                setContentTitle("음악 재생")
                setContentText("음원 재생 중...")
                setContentIntent(mainPendingIntent)
                style = (
                        Notification.MediaStyle().setShowActionsInCompactView(0, 1, 2)
                        )
                setVisibility(Notification.VISIBILITY_PUBLIC)
                addAction(
                    Notification.Action.Builder(pauseIcon, "Pause", pausePendingIntent).build()
                )
                addAction(Notification.Action.Builder(playIcon, "Play", playPendingIntent).build())
                addAction(Notification.Action.Builder(stopIcon, "Stop", stopPendingIntent).build())
            }.build()
        } else {
            NotificationCompat.Builder(this, MEDIA_CHANNEL_ID).apply {
                setSmallIcon(R.drawable.baseline_stars_24)
                setContentTitle("음악 재생")
                setContentText("음원 재생 중...")
                setContentIntent(mainPendingIntent)
            }.build()
        }

        Log.i("MediaPlayerService", "notification $notification")

        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            MEDIA_PLAYER_PLAY -> {
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer.create(this, R.raw.sample).apply {
                        isLooping = true
                    }
                }
                mediaPlayer?.start()
            }

            MEDIA_PLAYER_PAUSE -> {
                mediaPlayer?.pause()
            }

            MEDIA_PLAYER_STOP -> {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
                stopSelf() // 명시적으로 서비스 종료 선언 필요
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        mediaPlayer?.apply {
            stop()
            release()
        }
        mediaPlayer = null
        super.onDestroy()
    }
}