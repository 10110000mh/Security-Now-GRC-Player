package am.h10110000.securitynow.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun EpisodeSelectionDialog(
    onEpisodeSelected: (Int) -> Unit,
    onDismissRequest: () -> Unit
) {
    var episodeInput by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    // AlertDialog for episode selection
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Select Episode") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = episodeInput,
                    onValueChange = {
                        episodeInput = it
                        showError = false
                    },
                    label = { Text("Episode Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = showError,
                    supportingText = if (showError) {
                        { Text("Please enter a valid episode number") }
                    } else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val episodeNumber = episodeInput.toIntOrNull()
                    if (episodeNumber != null && episodeNumber > 0) {
                        onEpisodeSelected(episodeNumber)
                        onDismissRequest() // Close the dialog
                    } else {
                        showError = true
                    }
                }
            ) {
                Text("Load Episode")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}
