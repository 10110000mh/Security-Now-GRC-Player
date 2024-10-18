package am.h10110000.securitynow.data
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class SleepTimer {
    private var timerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    private val _remainingTime = MutableStateFlow<Duration?>(null)
    val remainingTime: StateFlow<Duration?> = _remainingTime

    fun startTimer(duration: Duration, onComplete: () -> Unit) {
        cancelTimer()

        timerJob = scope.launch {
            var remaining = duration
            while (remaining > Duration.ZERO) {
                _remainingTime.value = remaining
                delay(1.seconds)
                remaining -= 1.seconds
            }
            _remainingTime.value = null
            onComplete()
        }
    }

    fun cancelTimer() {
        timerJob?.cancel()
        timerJob = null
        _remainingTime.value = null
    }
}
