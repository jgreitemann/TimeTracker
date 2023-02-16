import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private fun LocalTime.roundDown() = LocalTime(hour, minute)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WorkLogEntry(interval: DateTimeInterval, onEdit: (DateTimeInterval) -> Unit = {}, modifier: Modifier = Modifier) {
    val startTime = interval.start.toLocalDateTime(TimeZone.currentSystemDefault()).time.roundDown()
    val endTime = interval.end.toLocalDateTime(TimeZone.currentSystemDefault()).time.roundDown()
    var hovered by remember { mutableStateOf(false) }
    Row(
        modifier
            .width(180.dp)
            .onPointerEvent(PointerEventType.Enter) { hovered = true }
            .onPointerEvent(PointerEventType.Exit) { hovered = false },
        horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.End),
    ) {

        Text(
            "$startTime — $endTime",
            Modifier.padding(vertical = 4.dp)
        )

        IconButton(
            onClick = { onEdit(interval) },
            modifier = Modifier.size(24.dp).alpha(if (hovered) {1.0f} else {0.0f}),
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit work log",
                tint = MaterialTheme.colors.primaryVariant,
            )
        }

    }
}

@Composable
@Preview
fun WorkLogEntryPreview() = Column {
    val modifier = Modifier.border(width = 1.dp, color = Color.Red)

    WorkLogEntry(
        DateTimeInterval(
            start = Instant.parse("2023-02-12T08:12:00+01"),
            end = Instant.parse("2023-02-12T18:45:00+01")
        ),
        modifier = modifier,
    )

    WorkLogEntry(
        DateTimeInterval(
            start = Instant.parse("2023-02-12T11:11:00+01"),
            end = Instant.parse("2023-02-12T11:11:01+01")
        ),
        modifier = modifier,
    )

    WorkLogEntry(
        DateTimeInterval(
            start = Instant.parse("2023-02-12T23:44:00+01"),
            end = Instant.parse("2023-02-12T23:48:00+01")
        ),
        modifier = modifier,
    )
}
