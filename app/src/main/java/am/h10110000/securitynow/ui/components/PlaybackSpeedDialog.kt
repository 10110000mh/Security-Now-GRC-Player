package am.h10110000.securitynow.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PlaybackSpeedDialog(
    currentSpeed: Float,
    onDismiss: () -> Unit,
    onSpeedSelected: (Float) -> Unit
) {
    var sliderPosition by remember { mutableFloatStateOf(currentSpeed) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Playback Speed") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = String.format("%.1fx", sliderPosition),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Slider(
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    valueRange = 0.5f..3f,
                    steps = 25, // 0.1 increments
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                TextButton(
                    onClick = { sliderPosition = 1f },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Reset to 1x")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSpeedSelected(sliderPosition)
                    onDismiss()
                }
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}