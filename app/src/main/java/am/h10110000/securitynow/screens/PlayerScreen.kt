package am.h10110000.securitynow.screens

import am.h10110000.securitynow.data.EpisodeManager
import am.h10110000.securitynow.service.PlayerService
import am.h10110000.securitynow.ui.components.EpisodeSelectionDialog
import am.h10110000.securitynow.ui.components.PlaybackSpeedDialog
import am.h10110000.securitynow.ui.components.PlayerControls
import am.h10110000.securitynow.ui.components.SleepTimerDialog
import am.h10110000.securitynow.ui.components.TimerDisplay
import androidx.compose.foundation.background
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
    val currentEpisode by playerService.currentEpisode.collectAsState()

    val isPlaying by playerService.isPlaying.collectAsState()
    val remainingTime by playerService.remainingTime.collectAsState()
    val currentPosition by playerService.currentPosition.collectAsState()
    val duration by playerService.duration.collectAsState()
    var showTimerDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var showSpeedDialog by remember { mutableStateOf(false) }
    val playbackSpeed by playerService.preferencesManager.playbackSpeed.collectAsState()
    var showEpisodeDialog by remember { mutableStateOf(false) }  // State for episode selection dialog
    var isAutoPlayEnabled = playerService.preferencesManager.getAutoPlay()
    val backgroundColor = MaterialTheme.colorScheme.surface

    // State for the toggle switch

    // State for slider
    var isSliding by remember { mutableStateOf(false) }
    var slidingPosition by remember { mutableStateOf(0f) }

    // Calculate the position to display (either sliding position or current playback position)
    val displayPosition = if (isSliding) {
        slidingPosition.toLong()
    } else {
        currentPosition
    }
    LaunchedEffect(episodeNumber) {
        playerService.setEpisode(episodeNumber)
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { showEpisodeDialog = true }) { // Trigger the dialog
                Text(
                    text = "Episode $currentEpisode",
                    style = MaterialTheme.typography.headlineMedium
                )
            }


        }

        // Episode title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (playerService.title.isEmpty() || playerService.title == "null") "Loading..." else playerService.title,
                style = MaterialTheme.typography.headlineSmall
            )
        }

        // Progress slider - only show if duration is valid
        if (duration > 0) {
            Slider(
                value = displayPosition.toFloat(),
                onValueChange = { newPosition ->
                    isSliding = true
                    slidingPosition = newPosition.coerceIn(0f, duration.toFloat())
                },
                onValueChangeFinished = {
                    // Only seek when the slider is released
                    if (isSliding) {
                        playerService.seekTo(slidingPosition.toLong())
                        isSliding = false
                    }
                },
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
                Text(formatDuration(displayPosition))
                Text(formatDuration(duration))
            }
        }

        PlayerControls(
            isPlaying = isPlaying,
            onPlayPause = { playerService.togglePlayPause() },
            onRewind = { playerService.seekBackward() },
            onFastForward = { playerService.seekForward() },
            onBack = {
                if (playerService.onBack()) {
                    playerService.changeEpisode(-1)
                }
            },
            onNext = {
                playerService.onNext()
                playerService.changeEpisode(+1)
            },
            modifier = Modifier.padding(vertical = 32.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,

            ) {
            TextButton(onClick = { showSpeedDialog = true }) {
                Text("Speed: ${String.format("%.1fx", playbackSpeed)}")
            }

            TimerDisplay(
                remainingTime = remainingTime,
                onClockClick = { showTimerDialog = true },
                onTimerClick = { showCancelDialog = true }
            )

            Text("Auto-Play")
            Switch(
                checked = isAutoPlayEnabled,
                onCheckedChange = { playerService.preferencesManager.saveAutoPlay(it) },

                )


        }
    }

    // Dialogs
    if (showTimerDialog) {
        SleepTimerDialog(
            onDismiss = { showTimerDialog = false },
            onSetTimer = { minutes ->
                playerService.startSleepTimer(minutes)
                showTimerDialog = false
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
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    if (showSpeedDialog) {
        PlaybackSpeedDialog(
            currentSpeed = playbackSpeed,
            onDismiss = { showSpeedDialog = false },
            onSpeedSelected = {
                playerService.setPlaybackSpeed(it)
                showSpeedDialog = false
            }
        )
    }
    // Dialog for selecting episode
    if (showEpisodeDialog) {
        EpisodeSelectionDialog(
            onEpisodeSelected = { selectedEpisode ->
                // Load the selected episode

                playerService.setEpisode(selectedEpisode)
                val url = EpisodeManager.generateEpisodeUrl(selectedEpisode)
                playerService.loadAndPlay(url, selectedEpisode)
                showEpisodeDialog = false  // Dismiss the dialog
            },
            onDismissRequest = { showEpisodeDialog = false } // Handle dialog dismiss
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