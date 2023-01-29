import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DateTimeIntervalKtTest {
    private val lunchBreak =
        DateTimeInterval(Instant.parse("2023-01-29T12:00:00Z"), Instant.parse("2023-01-29T13:00:00Z"))
    private val eating = DateTimeInterval(Instant.parse("2023-01-29T12:15:00Z"), Instant.parse("2023-01-29T12:35:00Z"))
    private val coffeeBreak =
        DateTimeInterval(Instant.parse("2023-01-29T12:45:00Z"), Instant.parse("2023-01-29T13:15:00Z"))
    private val meeting = DateTimeInterval(Instant.parse("2023-01-29T11:00:00Z"), Instant.parse("2023-01-29T12:00:00Z"))
    private val teaBreak =
        DateTimeInterval(Instant.parse("2023-01-29T15:00:00Z"), Instant.parse("2023-01-29T15:30:00Z"))

    @Test
    fun `Instants within the time interval are contained`() {
        assertTrue(Instant.parse("2023-01-29T12:30:00Z") in lunchBreak)
        assertTrue(Instant.parse("2023-01-29T12:15:00Z") in lunchBreak)
        assertTrue(Instant.parse("2023-01-29T12:45:00Z") in lunchBreak)
    }

    @Test
    fun `Interval limits are contained`() {
        assertTrue(lunchBreak.start in lunchBreak)
        assertTrue(lunchBreak.end in lunchBreak)
    }

    @Test
    fun `Instants outside of the time interval are NOT contained`() {
        assertTrue(Instant.parse("2023-01-29T11:59:59Z") !in lunchBreak)
        assertTrue(Instant.parse("2023-01-29T13:00:01Z") !in lunchBreak)
        assertTrue(Instant.DISTANT_PAST !in lunchBreak)
        assertTrue(Instant.DISTANT_FUTURE !in lunchBreak)
    }

    @Test
    fun `Overlapping intervals can be merged into one`() {
        assertEquals(DateTimeInterval(lunchBreak.start, coffeeBreak.end), lunchBreak merge coffeeBreak)
        assertEquals(DateTimeInterval(lunchBreak.start, coffeeBreak.end), coffeeBreak merge lunchBreak)
    }

    @Test
    fun `Fully contained interval is 'swallowed' when merged`() {
        assertEquals(lunchBreak, lunchBreak merge eating)
        assertEquals(lunchBreak, eating merge lunchBreak)
    }

    @Test
    fun `Consecutive, non-overlapping intervals can be merged into one`() {
        assertEquals(DateTimeInterval(meeting.start, lunchBreak.end), meeting merge lunchBreak)
        assertEquals(DateTimeInterval(meeting.start, lunchBreak.end), lunchBreak merge meeting)
    }

    @Test
    fun `Non-consecutive, non-overlapping intervals cannot be merged, returning null`() {
        assertNull(lunchBreak merge teaBreak)
        assertNull(teaBreak merge lunchBreak)
    }

}