package am.h10110000.securitynow.service

import am.h10110000.securitynow.R
import am.h10110000.securitynow.data.EpisodeManager
import am.h10110000.securitynow.data.PreferencesManager
import am.h10110000.securitynow.data.SleepTimer
import am.h10110000.securitynow.globalVariable
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes


class PlayerService : Service() {
    lateinit var preferencesManager: PreferencesManager
    private var currentEpisodeNumber: Int = -1
    private var progressUpdateJob: Job? = null
    public var title = ""

    // Add to existing properties
    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition
    private var _currentEpisode = MutableStateFlow(globalVariable)
    var currentEpisode: StateFlow<Int> = _currentEpisode
    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration

    private var player: ExoPlayer? = null
    private val sleepTimer = SleepTimer()
    private lateinit var notificationManager: PlayerNotificationManager
    private lateinit var mediaSession: MediaSession

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying
    val remainingTime = sleepTimer.remainingTime

    // Binder given to clients
    inner class PlayerBinder : Binder() {
        // Return this instance of PlayerService so clients can call public methods
        fun getService(): PlayerService = this@PlayerService
    }

    fun changeEpisode(num: Int) {
        _currentEpisode.value += num
    }

    fun setEpisode(num: Int) {
        _currentEpisode.value = num
    }

    // Binder instance for client communication
    private val binder = PlayerBinder()

    override fun onCreate() {
        super.onCreate()
        preferencesManager = PreferencesManager(this)
        player = ExoPlayer.Builder(this).build().apply {
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                    updateNotification()
                    updateProgressTracking(isPlaying)
                    _duration.value = duration
                    _currentPosition.value = player!!.currentPosition.coerceAtLeast(0L)
                    title = mediaMetadata.title.toString();
                }

            })

            // Set saved playback speed
            setPlaybackSpeed(preferencesManager.getPlaybackSpeed())
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

    }
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        when (intent?.action) {
//            PlayerActions.PLAY_PAUSE.toString() -> togglePlayPause()
//            PlayerActions.REWIND.toString() -> seekBackward()
//            PlayerActions.FAST_FORWARD.toString() -> seekForward()
//        }
//        return START_STICKY
//    }


    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    private fun updateProgressTracking(isPlaying: Boolean) {
        progressUpdateJob?.cancel()
        if (isPlaying) {
            progressUpdateJob = CoroutineScope(Dispatchers.Main).launch {
                while (isActive) {
                    player?.let { player ->
                        _currentPosition.value = player.currentPosition.coerceAtLeast(0L)

                        preferencesManager.saveEpisodeState(
                            currentEpisodeNumber,
                            player.currentPosition
                        )
                        if (player.currentPosition >= player.duration - 3500) {
                            // Call the function that should happen after playback finishes
                            if (preferencesManager.getAutoPlay()) {
                                onNext()
                                changeEpisode(1)
                            }
                            // Exit the loop once playback is finished
                        }
                    }

                    delay(1000) // Update every second
                }
            }
        }
    }

    fun startSleepTimer(minutes: Long) {
        sleepTimer.startTimer(minutes.minutes) {
            // Stop playback when timer completes
            player?.pause()
        }
    }

    fun cancelSleepTimer() {
        sleepTimer.cancelTimer()
    }




    fun loadAndPlay(url: String, episodeNumber: Int) {
        currentEpisodeNumber = episodeNumber
        player?.apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()

            // Restore position if it's the same episode
            if (episodeNumber == preferencesManager.getLastEpisodeNumber()) {
                val position = preferencesManager.getLastPlaybackPosition()
                seekTo(maxOf(0L, position - 15000)) // Resume 15 seconds earlier
            }

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
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(1, notification)

        }
    }

    fun setPlaybackSpeed(speed: Float) {
        player?.setPlaybackSpeed(speed)
        preferencesManager.savePlaybackSpeed(speed)
    }

    override fun onDestroy() {
        progressUpdateJob?.cancel()
        player?.let { player ->
            preferencesManager.saveEpisodeState(
                currentEpisodeNumber,
                player.currentPosition
            )
        }
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

    fun seekTo(toLong: Long) {
        val safeValue = toLong.coerceAtLeast(0) // Ensure minimum value is 1000ms
        player?.apply {
            seekTo(safeValue) // 20 seconds
        }
    }

    fun onBack(): Boolean {
        return if (player!!.currentPosition < 5000L) {
            val url = EpisodeManager.generateEpisodeUrl(currentEpisodeNumber - 1)
            loadAndPlay(url, currentEpisodeNumber - 1)
            true // Return true when successfully playing the previous episode
        } else {
            seekTo(0)
            false // Return false when seeking to the start
        }
    }

    fun onNext() {
        val url = EpisodeManager.generateEpisodeUrl(currentEpisodeNumber + 1)
        loadAndPlay(url, currentEpisodeNumber + 1)
    }


}

