package am.h10110000.securitynow.ui.components


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.time.Duration.Companion.minutes

@Composable
fun SleepTimerDialog(
    onDismiss: () -> Unit,
    onSetTimer: (Long) -> Unit
) {
    val timeOptions = listOf(15L, 30L, 45L, 60L)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Sleep Timer") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                timeOptions.forEach { minutes ->
                    Button(
                        onClick = {
                            onSetTimer(minutes)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("$minutes minutes")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}