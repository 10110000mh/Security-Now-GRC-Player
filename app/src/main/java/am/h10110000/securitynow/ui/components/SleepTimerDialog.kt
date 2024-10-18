package am.h10110000.securitynow.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue
import kotlin.time.Duration.Companion.minutes

@Composable
fun SleepTimerDialog(
    onDismiss: () -> Unit,
    onSetTimer: (Long) -> Unit
) {
    var minutesInput by remember { mutableStateOf(TextFieldValue("")) }
    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Sleep Timer") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = minutesInput,
                    onValueChange = {
                        minutesInput = it
                        errorMessage = "" // Clear error message when input changes
                    },
                    label = { Text("Enter minutes") },
                    isError = errorMessage.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )
                if (errorMessage.isNotEmpty()) {
                    Text(errorMessage, color = MaterialTheme.colorScheme.error)
                }

                Button(
                    onClick = {
                        val minutes = minutesInput.text.toLongOrNull()
                        if (minutes != null && minutes > 0) {
                            onSetTimer(minutes)
                            onDismiss()
                        } else {
                            errorMessage = "Please enter a valid number of minutes."
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Set Timer")
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
