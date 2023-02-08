import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import java.time.format.TextStyle
import java.util.*


@Composable
fun WorkLogList(workLog: List<DateTimeInterval>) {
    LazyColumn {
        items(workLog
            .groupBy { it.start.toLocalDateTime(TimeZone.currentSystemDefault()).date }
            .asIterable()
            .reversed()
        ) { (date, intervals) ->
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val weekDay = date.dayOfWeek.getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault())
                    val month = date.month.getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault())
                    val totalTime = intervals.map { it.toPeriod() }.reduce { acc, period -> acc + period }

                    Text("$weekDay, $month ${date.dayOfMonth}")
                    Text(
                        "${totalTime.hours}h ${totalTime.minutes}min",
                        color = MaterialTheme.colors.primaryVariant,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Column {
                    intervals.forEach { (start, end) ->
                        val startTime = start.toLocalDateTime(TimeZone.currentSystemDefault()).time.roundDown()
                        val endTime = end.toLocalDateTime(TimeZone.currentSystemDefault()).time.roundDown()

                        Text(
                            "$startTime — $endTime",
                            Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }

            Divider(Modifier.padding(PaddingValues(horizontal = 20.dp, vertical = 10.dp)))
        }
    }
}

private fun LocalTime.roundDown() = LocalTime(hour, minute)

@Composable
@Preview
fun WorkLogListPreview() {
    MaterialTheme {
        WorkLogList(
            listOf(
                DateTimeInterval(Instant.parse("2023-01-28T15:45:32+01"), Instant.parse("2023-01-28T18:58:23+01")),
                DateTimeInterval(Instant.parse("2023-01-28T20:12:13+01"), Instant.parse("2023-01-28T22:14:03+01")),
                DateTimeInterval(Instant.parse("2023-01-28T22:32:49+01"), Instant.parse("2023-01-29T01:48:09+01")),
                DateTimeInterval(Instant.parse("2023-01-29T14:42:00+01"), Instant.parse("2023-01-29T15:26:28+01")),
                DateTimeInterval(Instant.parse("2023-01-29T15:59:07+01"), Instant.parse("2023-01-29T17:05:52+01")),
                DateTimeInterval(Instant.parse("2023-02-01T21:04:12+01"), Instant.parse("2023-02-01T22:59:08+01")),
            )
        )
    }
}