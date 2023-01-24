import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.delay
import kotlin.time.TimeSource
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

enum class State {
    Pausing {
        override fun toggled() = Working
    },
    Working {
        override fun toggled() = Pausing
    };

    abstract fun toggled(): State
}

@OptIn(ExperimentalTime::class)
@Composable
@Preview
fun App() {
    var start = TimeSource.Monotonic.markNow()
    var startDuration = Duration.ZERO
    var state by remember { mutableStateOf(State.Pausing) }
    var duration by remember { mutableStateOf(Duration.ZERO) }

    LaunchedEffect(key1 = state) {
        if (state == State.Working) {
            startDuration += duration
            while (true) {
                delay(1000)
                duration = start.elapsedNow() + startDuration
            }
        }
    }

    MaterialTheme {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = duration.toComponents { hours, minutes, seconds, _ ->
                    "%02d:%02d:%02d".format(
                        hours,
                        minutes,
                        seconds
                    )
                },
                style = MaterialTheme.typography.h2,
            )

            Row {

                Button(
                    onClick = {
                        state = state.toggled()
                        if (state == State.Working) {
                            start = TimeSource.Monotonic.markNow()
                        }
                    },
                    modifier = Modifier.padding(10.dp),
                ) {
                    Text(
                        when (state) {
                            State.Pausing -> if (duration == Duration.ZERO) {
                                "Start working"
                            } else {
                                "Resume work"
                            }

                            State.Working -> "Take a break"
                        }
                    )
                }

                Button(
                    onClick = {
                        state = State.Pausing
                        duration = Duration.ZERO
                    },
                    modifier = Modifier.padding(10.dp),
                    enabled = state == State.Working || duration > Duration.ZERO
                ) {
                    Text("End workday")
                }

            }
        }
    }
}

fun main() = application {
    Window(
        title = "Time Tracker",
        onCloseRequest = ::exitApplication,
    ) {
        App()
    }
}
