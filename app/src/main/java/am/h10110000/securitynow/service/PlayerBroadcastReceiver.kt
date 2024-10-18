package am.h10110000.securitynow.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlin.math.log

class PlayerBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            PlayerActions.PLAY_PAUSE.toString() -> {
                // Handle the play/pause action, possibly using a Service
                val serviceIntent = Intent(context, PlayerService::class.java).apply {
                    action = PlayerActions.PLAY_PAUSE.toString()
                }
                context?.startService(serviceIntent)
            }
            PlayerActions.REWIND.toString() -> {
                // Handle rewind action
                val serviceIntent = Intent(context, PlayerService::class.java).apply {
                    action = PlayerActions.REWIND.toString()
                }
                context?.startService(serviceIntent)
            }
            PlayerActions.FAST_FORWARD.toString() -> {
                // Handle fast-forward action
                val serviceIntent = Intent(context, PlayerService::class.java).apply {
                    action = PlayerActions.FAST_FORWARD.toString()
                }
                context?.startService(serviceIntent)
            }
        }
    }
}
