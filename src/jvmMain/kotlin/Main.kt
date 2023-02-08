
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
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
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

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
fun App(
    model: WorkLogStore,
    currentDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
) {
    var state by remember { mutableStateOf(State.Pausing) }

    Timer(running = state == State.Working) { interval ->
        model.logWork(interval)
    }

    MaterialTheme {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = CutCornerShape(0),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimerText(model.periodForDate(currentDate), style = MaterialTheme.typography.h2)

                    Row {

                        Button(
                            onClick = {
                                state = state.toggled()
                            },
                            modifier = Modifier.padding(10.dp),
                        ) {
                            Text(
                                when (state) {
                                    State.Pausing -> if (model.workLog.isEmpty()) {
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
                                model.clear()
                            },
                            modifier = Modifier.padding(10.dp),
                            enabled = state == State.Working || model.workLog.isNotEmpty()
                        ) {
                            Text("Clear work log")
                        }

                    }
                }
            }

            WorkLogList(model.workLog)

        }
    }
}

@Composable
@Preview
fun AppPreview() {
    App(FakeWorkLogStore, LocalDate.parse("2023-01-30"))
}


fun main() = application {
    Window(
        title = "Time Tracker",
        onCloseRequest = ::exitApplication,
    ) {
        App(JsonFileWorkLogStore())
    }
}
