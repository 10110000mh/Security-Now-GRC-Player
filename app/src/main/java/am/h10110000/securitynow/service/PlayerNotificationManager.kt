package am.h10110000.securitynow.service
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.media3.common.Player
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper
import am.h10110000.securitynow.MainActivity
import am.h10110000.securitynow.R
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi

class PlayerNotificationManager(private val context: Context) {
    companion object {
        private const val CHANNEL_ID = "podcast_playback_channel"
        private const val NOTIFICATION_ID = 1
        private const val REQUEST_CODE = 100
    }

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Podcast Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Controls for podcast playback"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    @OptIn(UnstableApi::class)
    fun getNotification(
        player: Player,
        mediaSession: MediaSession,
        episodeNumber: Int
    ): Notification {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_podcast_nobg)
            .setContentTitle("Episode $episodeNumber")
            .setContentText("Security Now")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setStyle(
                MediaStyleNotificationHelper.MediaStyle(mediaSession)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        // Add playback controls
        builder.addAction(
            R.drawable.ic_rewind,
            "Rewind",
            createActionPendingIntent(PlayerActions.REWIND)
        )

        builder.addAction(
            if (player.isPlaying) R.drawable.ic_pause else R.drawable.ic_play,
            if (player.isPlaying) "Pause" else "Play",
            createActionPendingIntent(PlayerActions.PLAY_PAUSE)
        )

        builder.addAction(
            R.drawable.ic_fast_forward,
            "Fast Forward",
            createActionPendingIntent(PlayerActions.FAST_FORWARD)
        )

        return builder.build()
    }

    private fun createActionPendingIntent(action: PlayerActions): PendingIntent {
        val intent = Intent(action.toString()).apply {
            setPackage(context.packageName)
        }
        return PendingIntent.getBroadcast(
            context,
            action.ordinal,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }
}
