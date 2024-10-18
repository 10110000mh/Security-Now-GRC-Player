package am.h10110000.securitynow.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import am.h10110000.securitynow.R
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape

@Composable
fun PlayerControls(
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onRewind: () -> Unit,
    onFastForward: () -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface, CircleShape)
        , // Adds horizontal spacing,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,


    ) {
        IconButton(

            onClick = onBack,
            modifier = Modifier
                .size(48.dp)
                .padding(horizontal = 8.dp) // Adds horizontal spacing


        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Fast forward 20 seconds"
            )
        }
        IconButton(
            onClick = onRewind,
            modifier = Modifier.size(55.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_rewind),
                contentDescription = "Rewind 20 seconds"
            )
        }

        IconButton(
            onClick = onPlayPause,
            modifier = Modifier.size(90.dp)
        ) {
            Icon(
                painter = painterResource(
                    id = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                ),
                contentDescription = if (isPlaying) "Pause" else "Play"
            )
        }

        IconButton(
            onClick = onFastForward,
            modifier = Modifier.size(55.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_fast_forward),
                contentDescription = "Fast forward 20 seconds"
            )
        }
        IconButton(
            onClick = onNext,
            modifier = Modifier
                .size(48.dp)
                .padding(horizontal = 8.dp) // Adds horizontal spacing

        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_next),
                contentDescription = "Fast forward 20 seconds"
            )
        }
    }
}