package am.h10110000.securitynow.service

import am.h10110000.securitynow.R
import am.h10110000.securitynow.data.SleepTimer
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration.Companion.minutes


class PlayerService : Service() {

    fun startSleepTimer(minutes: Long) {
        sleepTimer.startTimer(minutes.minutes) {
            // Stop playback when timer completes
            player?.pause()
        }
    }

    fun cancelSleepTimer() {
        sleepTimer.cancelTimer()
    }

    private var player: ExoPlayer? = null
    private val sleepTimer = SleepTimer()
    private lateinit var notificationManager: PlayerNotificationManager
    private lateinit var mediaSession: MediaSession
    private var currentEpisodeNumber: Int = 0

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying
    val remainingTime = sleepTimer.remainingTime

    // Binder given to clients
    inner class PlayerBinder : Binder() {
        // Return this instance of PlayerService so clients can call public methods
        fun getService(): PlayerService = this@PlayerService
    }

    // Binder instance for client communication
    private val binder = PlayerBinder()

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                PlayerActions.PLAY_PAUSE.toString() -> togglePlayPause()
                PlayerActions.REWIND.toString() -> seekBackward()
                PlayerActions.FAST_FORWARD.toString() -> seekForward()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        player = ExoPlayer.Builder(this).build().apply {
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                    updateNotification()
                }
            })
        }

        mediaSession = MediaSession.Builder(this, player!!)
            .build()

        notificationManager = PlayerNotificationManager(this)

        // Register broadcast receiver for notification actions
        val intentFilter = IntentFilter().apply {
            PlayerActions.values().forEach { action ->
                addAction(action.toString())
            }
        }
        registerReceiver(broadcastReceiver, intentFilter)
    }

    fun loadAndPlay(url: String, episodeNumber: Int) {
        currentEpisodeNumber = episodeNumber
        player?.apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
            play()
        }
        startForeground()
    }

    private fun startForeground() {
        player?.let { player ->
            val notification = notificationManager.getNotification(
                player,
                mediaSession,
                currentEpisodeNumber
            )
            startForeground(1, notification)
        }
    }

    private fun updateNotification() {
        player?.let { player ->
            val notification = notificationManager.getNotification(
                player,
                mediaSession,
                currentEpisodeNumber
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(1, notification)

        }
    }

    override fun onDestroy() {
        unregisterReceiver(broadcastReceiver)
        mediaSession.release()
        player?.release()
        player = null
        super.onDestroy()
    }


    fun togglePlayPause() {
        player?.apply {
            if (isPlaying) pause() else play()
        }
    }

    fun seekForward() {
        player?.apply {
            seekTo(currentPosition + 15000) // 20 seconds
        }
    }

    fun seekBackward() {
        player?.apply {
            seekTo(currentPosition - 15000) // 20 seconds
        }
    }


}
