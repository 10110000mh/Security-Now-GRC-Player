package am.h10110000.securitynow
import am.h10110000.securitynow.screens.EpisodeSelectionScreen
import am.h10110000.securitynow.screens.PlayerScreen
import am.h10110000.securitynow.service.PlayerService
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import am.h10110000.securitynow.ui.theme.PodcastPlayerTheme
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
class MainActivity : ComponentActivity() {
    private var playerService: PlayerService? = null

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
                        var selectedEpisode by remember { mutableStateOf<Int?>(null) }

                        if (selectedEpisode == null) {
                            EpisodeSelectionScreen { episodeNumber ->
                                selectedEpisode = episodeNumber
                            }
                        } else {
                            PlayerScreen(
                                episodeNumber = selectedEpisode!!,
                                playerService = service
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Bind to PlayerService
        Intent(this, PlayerService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }

        // Initial UI setup
        updateUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }
}