package am.h10110000.securitynow.screens

import am.h10110000.securitynow.R
import am.h10110000.securitynow.data.EpisodeManager
import am.h10110000.securitynow.highQulity
import am.h10110000.securitynow.service.PlayerService
import am.h10110000.securitynow.ui.components.*
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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
    val playbackSpeed by playerService.preferencesManager.playbackSpeed.collectAsState()

    // Dialog states
    var showTimerDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var showSpeedDialog by remember { mutableStateOf(false) }
    var showEpisodeDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showDownloadDialog by remember { mutableStateOf(false) }

    // Settings states
    var isAutoPlayEnabled by remember { mutableStateOf(playerService.preferencesManager.getAutoPlay()) }
    var isHighQualityEnabled by remember { mutableStateOf(playerService.preferencesManager.getHighQuality()) }
    var isDarkThemeEnabled by remember { mutableStateOf(playerService.preferencesManager.getDarkTheme()) }

    // Slider states
    var isSliding by remember { mutableStateOf(false) }
    var slidingPosition by remember { mutableStateOf(0f) }
    val displayPosition = if (isSliding) slidingPosition.toLong() else currentPosition

    LaunchedEffect(episodeNumber) {
        playerService.setEpisode(episodeNumber)
        val url = EpisodeManager.generateEpisodeUrl(episodeNumber)
        Log.d("badsoure", "PlayerScreen: " + url)
        playerService.loadAndPlay(url, episodeNumber)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar with episode selector and action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { showEpisodeDialog = true }) {
                Text(
                    text = "Episode $currentEpisode",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = { showDownloadDialog = true }) {
                    Icon( painter = painterResource(id = R.drawable.ic_download), contentDescription = "Download")
                }
                IconButton(onClick = { showSettingsDialog = true }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        }

        // Episode title
        Text(
            text = if (playerService.title.isEmpty() || playerService.title == "null")
                "Loading..." else playerService.title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Progress slider
        if (duration > 0) {
            Slider(
                value = displayPosition.toFloat(),
                onValueChange = { newPosition ->
                    isSliding = true
                    slidingPosition = newPosition.coerceIn(0f, duration.toFloat())
                },
                onValueChangeFinished = {
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
                playerService.changeEpisode(1)
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

    if (showEpisodeDialog) {
        EpisodeSelectionDialog(
            onEpisodeSelected = { selectedEpisode ->
                playerService.setEpisode(selectedEpisode)
                val url = EpisodeManager.generateEpisodeUrl(selectedEpisode)
                playerService.loadAndPlay(url, selectedEpisode)
                showEpisodeDialog = false
            },
            onDismissRequest = { showEpisodeDialog = false }
        )
    }

    if (showSettingsDialog) {
        SettingsDialog(
            isAutoPlayEnabled = isAutoPlayEnabled,
            isHighQualityEnabled = isHighQualityEnabled,
            isDarkThemeEnabled = isDarkThemeEnabled,
            onAutoPlayChanged = { newValue ->
                isAutoPlayEnabled = newValue
                playerService.preferencesManager.saveAutoPlay(newValue)
            },
            onQualityChanged = { newValue ->
                isHighQualityEnabled = newValue
                highQulity = newValue
                playerService.player?.let {
                    playerService.preferencesManager.saveEpisodeState(
                        currentEpisode,
                        it.currentPosition
                    )
                    val url = EpisodeManager.generateEpisodeUrl(currentEpisode)

                    playerService.loadAndPlay(url,currentEpisode)
                }
                playerService.preferencesManager.saveHighQuality(newValue)
            },
            onThemeChanged = { newValue ->
                isDarkThemeEnabled = newValue
                playerService.preferencesManager.saveDarkTheme(newValue)
            },
            onDismiss = { showSettingsDialog = false }
        )
    }

    if (showDownloadDialog) {
        DownloadDialog(
            onDismiss = { showDownloadDialog = false },
            onDownload = { options ->
                // Implement download functionality here
                // You can access all download options from the options parameter
            }
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