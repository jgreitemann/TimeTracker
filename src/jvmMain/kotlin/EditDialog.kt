import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@Composable
fun DateTimePicker(dateTime: LocalDateTime, onDateTimeChange: (LocalDateTime) -> Unit) {
    Row {
        TextField(
            "${dateTime.hour}",
            onValueChange = {
                onDateTimeChange(dateTime.updated(hour = it.toIntOrNull()))
            })
        Text(":")
        TextField(
            "${dateTime.minute}",
            onValueChange = {
                onDateTimeChange(dateTime.updated(minute = it.toIntOrNull()))
            })
    }
}

@Composable
fun EditDialog(
    originalInterval: DateTimeInterval,
    onClose: () -> Unit,
    onUpdate: (DateTimeInterval?, DateTimeInterval?) -> Unit
) {
    val tz = TimeZone.currentSystemDefault()
    var startDateTime by remember { mutableStateOf(originalInterval.start.toLocalDateTime(tz)) }
    var endDateTime by remember { mutableStateOf(originalInterval.end.toLocalDateTime(tz)) }
    val resultingInterval = DateTimeInterval(start = startDateTime.toInstant(tz), end = endDateTime.toInstant(tz))

    Dialog(
        onCloseRequest = onClose,
        title = "Edit work log entry"
    ) {
        Column {
            DateTimePicker(startDateTime, onDateTimeChange = { startDateTime = it })
            DateTimePicker(endDateTime, onDateTimeChange = { endDateTime = it })

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onClose) {
                    Text("Cancel")
                }

                Spacer(Modifier.width(40.dp))

                Button(
                    onClick = {
                        onClose()
                        onUpdate(originalInterval, null)
                    },
                    colors = buttonColors(backgroundColor = MaterialTheme.colors.error)
                ) {
                    Text("Delete")
                }

                Spacer(Modifier.width(20.dp))

                Button(
                    onClick = {
                        onClose()
                        onUpdate(
                            originalInterval,
                            resultingInterval,
                        )
                    },
                    enabled = originalInterval != resultingInterval
                ) {
                    Text("Save")
                }
            }
        }
    }
}

private fun LocalDateTime.updated(
    year: Int? = null,
    monthNumber: Int? = null,
    dayOfMonth: Int? = null,
    hour: Int? = null,
    minute: Int? = null
) = LocalDateTime(
    year ?: this.year,
    monthNumber ?: this.monthNumber,
    dayOfMonth ?: this.dayOfMonth,
    hour ?: this.hour,
    minute ?: this.minute
)
