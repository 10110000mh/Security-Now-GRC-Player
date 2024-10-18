package am.h10110000.securitynow.screens
import am.h10110000.securitynow.data.EpisodeManager
import am.h10110000.securitynow.service.PlayerService
import am.h10110000.securitynow.ui.components.PlayerControls
import am.h10110000.securitynow.ui.components.SleepTimerDialog
import am.h10110000.securitynow.ui.components.TimerDisplay
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PlayerScreen(
    episodeNumber: Int,
    playerService: PlayerService
) {
    val isPlaying by playerService.isPlaying.collectAsState()
    val remainingTime by playerService.remainingTime.collectAsState()
    var showTimerDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }

    LaunchedEffect(episodeNumber) {
        val url = EpisodeManager.generateEpisodeUrl(episodeNumber)
        playerService.loadAndPlay(url, episodeNumber)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Episode $episodeNumber",
                style = MaterialTheme.typography.headlineMedium
            )

            TimerDisplay(
                remainingTime = remainingTime,
                onClockClick = { showTimerDialog = true },
                onTimerClick = { showCancelDialog = true }
            )
        }

        PlayerControls(
            isPlaying = isPlaying,
            onPlayPause = { playerService.togglePlayPause() },
            onRewind = { playerService.seekBackward() },
            onFastForward = { playerService.seekForward() },
            modifier = Modifier.padding(vertical = 32.dp)
        )
    }

    if (showTimerDialog) {
        SleepTimerDialog(
            onDismiss = { showTimerDialog = false },
            onSetTimer = { minutes ->
                playerService.startSleepTimer(minutes)
            }
        )
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Sleep Timer?") },
            text = { Text("Do you want to cancel the sleep timer?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        playerService.cancelSleepTimer()
                        showCancelDialog = false
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCancelDialog = false }
                ) {
                    Text("No")
                }
            }
        )
    }
}