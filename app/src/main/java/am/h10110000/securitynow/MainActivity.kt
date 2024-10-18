package am.h10110000.securitynow

import am.h10110000.securitynow.data.PreferencesManager
import am.h10110000.securitynow.screens.PlayerScreen
import am.h10110000.securitynow.service.PlayerService
import am.h10110000.securitynow.ui.components.EpisodeSelectionDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import am.h10110000.securitynow.ui.theme.PodcastPlayerTheme
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
var globalVariable: Int = 0

class MainActivity : ComponentActivity() {
    private var playerService: PlayerService? = null
    lateinit var preferencesManager: PreferencesManager
    private var showDialog by mutableStateOf(false) // State to control dialog visibility

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesManager = PreferencesManager(this)
        globalVariable = preferencesManager.getLastEpisodeNumber()
        // Bind to PlayerService
        Intent(this, PlayerService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }

        // Initial UI setup
        updateUI()
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            // Get the PlayerService instance using the binder
            val binder = service as PlayerService.PlayerBinder
            playerService = binder.getService()

            // Update the UI now that we're connected
            updateUI()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            playerService = null
        }
    }

    private fun updateUI() {
        // Trigger UI recomposition with the connected service
        setContent {
            PodcastPlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    playerService?.let { service ->
                        var selectedEpisode = preferencesManager.getLastEpisodeNumber()
                        if (selectedEpisode == -1) {
                            // Show the dialog for episode selection
                            showDialog = true
                        } else {
                            PlayerScreen(
                                episodeNumber = selectedEpisode!!,
                                playerService = service
                            )
                        }

                        // Display the Episode Selection Dialog
                        if (showDialog) {
                            EpisodeSelectionDialog(
                                onEpisodeSelected = { episodeNumber ->
                                    selectedEpisode = episodeNumber
                                    preferencesManager.saveEpisodeState(
                                        episodeNumber,
                                        0L
                                    )
                                    showDialog = false // Dismiss the dialog
                                },
                                onDismissRequest = { showDialog = false } // Dismiss dialog on cancel
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }
}
