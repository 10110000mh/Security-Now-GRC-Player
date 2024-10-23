package am.h10110000.securitynow.ui.components
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import am.h10110000.securitynow.R
import androidx.compose.material3.TextButton
import kotlin.time.Duration

@Composable
fun TimerDisplay(
    remainingTime: Duration?,
    onClockClick: () -> Unit,
    onTimerClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .clickable(
                enabled = remainingTime != null,
                onClick = onTimerClick
            )
    ) {
        if (remainingTime == null) {
            TextButton(onClick =onClockClick) {
                Text("Set sleep timer")
            }
        } else {
            Text(
                text = formatDuration(remainingTime),
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

private fun formatDuration(duration: Duration): String {
    val totalMinutes = duration.inWholeMinutes
    val seconds = duration.inWholeSeconds % 60
    return "%d:%02d".format(totalMinutes, seconds)
}