package am.h10110000.securitynow.ui.components
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DownloadDialog(
    onDismiss: () -> Unit,
    onDownload: (DownloadOptions) -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var isHighQuality by remember { mutableStateOf(false) }
    var includeShowNotes by remember { mutableStateOf(false) }
    var includeTranscript by remember { mutableStateOf(false) }
    var startEpisode by remember { mutableStateOf("") }
    var endEpisode by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Download Episodes") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tabs
                TabRow(selectedTabIndex = selectedTabIndex) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 }
                    ) {
                        Text("Single")
                    }
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 }
                    ) {
                        Text("Batch")
                    }
                }

                // Common options
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (isHighQuality) "High Quality" else "Low Quality")
                    Switch(
                        checked = isHighQuality,
                        onCheckedChange = { isHighQuality = it }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = includeShowNotes,
                        onCheckedChange = { includeShowNotes = it }
                    )
                    Text("Include Show Notes")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = includeTranscript,
                        onCheckedChange = { includeTranscript = it }
                    )
                    Text("Include Transcript")
                }

                // Batch-specific options
                if (selectedTabIndex == 1) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = startEpisode,
                            onValueChange = { startEpisode = it },
                            label = { Text("Start Episode") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = endEpisode,
                            onValueChange = { endEpisode = it },
                            label = { Text("End Episode") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        val options = DownloadOptions(
                            isBatch = selectedTabIndex == 1,
                            isHighQuality = isHighQuality,
                            includeShowNotes = includeShowNotes,
                            includeTranscript = includeTranscript,
                            startEpisode = startEpisode.toIntOrNull(),
                            endEpisode = endEpisode.toIntOrNull()
                        )
                        onDownload(options)
                        onDismiss()
                    }
                ) {
                    Text("Download")
                }
            }
        }
    )
}

data class DownloadOptions(
    val isBatch: Boolean,
    val isHighQuality: Boolean,
    val includeShowNotes: Boolean,
    val includeTranscript: Boolean,
    val startEpisode: Int? = null,
    val endEpisode: Int? = null
)