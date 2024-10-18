package am.h10110000.securitynow.screens

import am.h10110000.securitynow.data.EpisodeManager
import am.h10110000.securitynow.service.PlayerService
import am.h10110000.securitynow.ui.components.PlaybackSpeedDialog
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
    val currentPosition by playerService.currentPosition.collectAsState()
    var showTimerDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var showSpeedDialog by remember { mutableStateOf(false) }
    val playbackSpeed by playerService.preferencesManager.playbackSpeed.collectAsState()
    val duration by playerService.duration.collectAsState()
    LaunchedEffect(episodeNumber) {
        val url = EpisodeManager.generateEpisodeUrl(episodeNumber)
        playerService.loadAndPlay(url, episodeNumber)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Episode title and timer row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Episode $episodeNumber",
                style = MaterialTheme.typography.headlineMedium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Playback speed button
                TextButton(onClick = { showSpeedDialog = true }) {
                    Text("${String.format("%.1fx", playbackSpeed)}")
                }

                TimerDisplay(
                    remainingTime = remainingTime,
                    onClockClick = { showTimerDialog = true },
                    onTimerClick = { /* ... */ }
                )
            }
        }

        // Progress slider
        Slider(
            value = currentPosition.toFloat(),
            onValueChange = { playerService.seekTo(it.toLong()) },
            valueRange = 0f..duration.toFloat(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )

        // Time display
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(formatDuration(currentPosition))
            Text(formatDuration(duration))
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
    if (showSpeedDialog) {
        PlaybackSpeedDialog(
            currentSpeed = playbackSpeed,
            onDismiss = { showSpeedDialog = false },
            onSpeedSelected = { playerService.setPlaybackSpeed(it) }
        )
    }
}

private fun formatDuration(ms: Long): String {
    val seconds = (ms / 1000) % 60
    val minutes = (ms / (1000 * 60)) % 60
    val hours = ms / (1000 * 60 * 60)
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%d:%02d", minutes, seconds)
    }
}