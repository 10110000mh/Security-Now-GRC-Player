package am.h10110000.securitynow.ui.components
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsDialog(
    isAutoPlayEnabled: Boolean,
    isHighQualityEnabled: Boolean,
    isDarkThemeEnabled: Boolean,
    onAutoPlayChanged: (Boolean) -> Unit,
    onQualityChanged: (Boolean) -> Unit,
    onThemeChanged: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Settings") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Auto Play Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Auto Play")
                    Switch(
                        checked = isAutoPlayEnabled,
                        onCheckedChange = onAutoPlayChanged
                    )
                }

                // Quality Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (isHighQualityEnabled) "High Quality" else "Low Quality")
                    Switch(
                        checked = isHighQualityEnabled,
                        onCheckedChange = onQualityChanged
                    )
                }

                // Theme Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (isDarkThemeEnabled) "Dark Theme" else "Light Theme")
                    Switch(
                        checked = isDarkThemeEnabled,
                        onCheckedChange = onThemeChanged
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}