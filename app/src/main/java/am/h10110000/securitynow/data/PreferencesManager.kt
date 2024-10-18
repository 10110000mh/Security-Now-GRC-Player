package am.h10110000.securitynow.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PreferencesManager(context: Context) {
    companion object {
        private const val PREFS_NAME = "podcast_player_prefs"
        private const val KEY_EPISODE_NUMBER = "episode_number"
        private const val KEY_PLAYBACK_POSITION = "playback_position"
        private const val KEY_PLAYBACK_SPEED = "playback_speed"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed

    init {
        _playbackSpeed.value = getPlaybackSpeed()
    }

    fun saveEpisodeState(episodeNumber: Int, position: Long) {
        prefs.edit().apply {
            putInt(KEY_EPISODE_NUMBER, episodeNumber)
            putLong(KEY_PLAYBACK_POSITION, position)
            apply()
        }
    }

    fun savePlaybackSpeed(speed: Float) {
        prefs.edit().putFloat(KEY_PLAYBACK_SPEED, speed).apply()
        _playbackSpeed.value = speed
    }

    fun getLastEpisodeNumber(): Int = prefs.getInt(KEY_EPISODE_NUMBER, -1)

    fun getLastPlaybackPosition(): Long = prefs.getLong(KEY_PLAYBACK_POSITION, 0L)

    fun getPlaybackSpeed(): Float = prefs.getFloat(KEY_PLAYBACK_SPEED, 1.0f)
}