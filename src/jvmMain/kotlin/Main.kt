import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlin.time.Duration

enum class State {
    Pausing {
        override fun toggled() = Working
    },
    Working {
        override fun toggled() = Pausing
    };

    abstract fun toggled(): State
}

@Composable
fun Timer(running: Boolean, onTimeChanged: (DateTimeInterval) -> Unit) {
    LaunchedEffect(key1 = running) {
        val start = Clock.System.now()
        while (running) {
            onTimeChanged(DateTimeInterval(start, end = Clock.System.now()))
            delay(1000)
        }
    }
}

@Composable
@Preview
fun App() {
    var state by remember { mutableStateOf(State.Pausing) }
    val intervals = remember { mutableStateListOf<DateTimeInterval>() }

    Timer(running = state == State.Working) { interval ->
        val merged = intervals.lastOrNull()?.merge(interval)
        if (merged != null) {
            intervals[intervals.lastIndex] = merged
        } else {
            intervals.add(interval)
        }
    }

    MaterialTheme {
        Column(
            Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = intervals.map { it.toDuration() }.fold(Duration.ZERO) { acc, next -> acc + next }
                            .toTimeString(),
                        style = MaterialTheme.typography.h1,
                    )

                    Row {

                        Button(
                            onClick = {
                                state = state.toggled()
                            },
                            modifier = Modifier.padding(10.dp),
                        ) {
                            Text(
                                when (state) {
                                    State.Pausing -> if (intervals.isEmpty()) {
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
                                intervals.clear()
                            },
                            modifier = Modifier.padding(10.dp),
                            enabled = state == State.Working || intervals.isNotEmpty()
                        ) {
                            Text("End workday")
                        }

                    }
                }
            }

            WorkLogList(intervals)

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
