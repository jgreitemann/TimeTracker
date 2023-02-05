import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil

data class DateTimeInterval(val start: Instant, val end: Instant) {
    operator fun contains(instant: Instant) = instant in start..end

    infix fun merge(other: DateTimeInterval): DateTimeInterval? = when {
        other.start in this -> DateTimeInterval(start, maxOf(end, other.end))
        other.end in this -> DateTimeInterval(minOf(start, other.start), end)
        start in other -> other
        else -> null
    }

    fun toPeriod() = start.periodUntil(end, timeZone = TimeZone.UTC)
}

fun DateTimePeriod.toTimeString() =
    "%02d:%02d:%02d".format(
        hours,
        minutes,
        seconds
    )