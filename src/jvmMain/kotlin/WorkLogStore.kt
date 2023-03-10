import kotlinx.datetime.*

interface WorkLogStore {
    val workLog: List<DateTimeInterval>

    fun update(old: DateTimeInterval?, new: DateTimeInterval?)
    fun clear()
}

fun WorkLogStore.periodForDate(date: LocalDate): DateTimePeriod =
    workLog
        .filter { it.start.toLocalDateTime(TimeZone.currentSystemDefault()).date == date }
        .map { it.toPeriod() }
        .fold(DateTimePeriod()) { acc, next -> acc + next }

object FakeWorkLogStore : WorkLogStore {
    override val workLog = listOf(
        DateTimeInterval(Instant.parse("2023-01-28T15:45:32+01"), Instant.parse("2023-01-28T18:58:23+01")),
        DateTimeInterval(Instant.parse("2023-01-28T20:12:13+01"), Instant.parse("2023-01-28T22:14:03+01")),
        DateTimeInterval(Instant.parse("2023-01-28T22:32:49+01"), Instant.parse("2023-01-29T01:48:09+01")),
        DateTimeInterval(Instant.parse("2023-01-29T14:42:00+01"), Instant.parse("2023-01-29T15:21:28+01")),
        DateTimeInterval(Instant.parse("2023-01-29T15:59:07+01"), Instant.parse("2023-01-29T17:05:52+01")),
        DateTimeInterval(Instant.parse("2023-01-30T21:04:12+01"), Instant.parse("2023-01-30T22:59:08+01")),
    )

    override fun update(old: DateTimeInterval?, new: DateTimeInterval?) = TODO("Not yet implemented")

    override fun clear() = TODO("Not yet implemented")
}