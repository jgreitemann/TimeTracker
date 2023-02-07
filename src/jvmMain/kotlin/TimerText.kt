import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import kotlinx.datetime.DateTimePeriod

@Composable
fun TimerText(
    period: DateTimePeriod,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
) {
    SubcomposeLayout { constraints ->
        val measuredWidth = subcompose("viewToMeasure") {
            Text("00:44:44", modifier, style = style)
        }[0]
            .measure(Constraints()).width.toDp()

        val contentPlaceable = subcompose("content") {
            Text(period.toTimeString(), modifier.width(measuredWidth), style = style)
        }[0].measure(constraints)
        layout(contentPlaceable.width, contentPlaceable.height) {
            contentPlaceable.place(0, 0)
        }
    }
}

@Composable
@Preview
fun TimerTextPreview() {
    val modifier = Modifier.border(1.dp, Color.Red)
    val periods = listOf(DateTimePeriod(), DateTimePeriod(minutes = 44, seconds = 44), DateTimePeriod(hours = 11, minutes = 11, seconds = 11))
    val styles = listOf(TextStyle(), MaterialTheme.typography.button, MaterialTheme.typography.h2, MaterialTheme.typography.h1)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        styles.forEach { style ->
            periods.forEach { period ->
                TimerText(period, modifier, style)
            }
        }
    }
}