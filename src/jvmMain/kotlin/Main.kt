import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.Card
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
fun Timer(duration: Duration, running: Boolean, onTimeElapsed: (Duration) -> Unit) {
    LaunchedEffect(key1 = duration, key2 = running) {
        if (running) {
            val start = TimeSource.Monotonic.markNow()
            delay(1000)
            onTimeElapsed(start.elapsedNow())
        }
    }

    Text(
        text = duration.toComponents { hours, minutes, seconds, _ ->
            "%02d:%02d:%02d".format(
                hours,
                minutes,
                seconds
            )
        },
        style = MaterialTheme.typography.h1,
    )
}

@Composable
@Preview
fun App() {
    var duration by remember { mutableStateOf(Duration.ZERO) }
    var state by remember { mutableStateOf(State.Pausing) }

    MaterialTheme {
        Column(
            Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Timer(duration, state == State.Working, onTimeElapsed = { elapsed -> duration += elapsed })

                    Row {

                        Button(
                            onClick = {
                                state = state.toggled()
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
