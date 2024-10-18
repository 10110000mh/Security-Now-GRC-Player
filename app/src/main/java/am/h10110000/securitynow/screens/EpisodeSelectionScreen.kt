package am.h10110000.securitynow.screens
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun EpisodeSelectionScreen(
    onEpisodeSelected: (Int) -> Unit
) {
    var episodeInput by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
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

        Button(
            onClick = {
                val episodeNumber = episodeInput.toIntOrNull()
                if (episodeNumber != null && episodeNumber > 0) {
                    onEpisodeSelected(episodeNumber)
                } else {
                    showError = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Load Episode")
        }
    }
}