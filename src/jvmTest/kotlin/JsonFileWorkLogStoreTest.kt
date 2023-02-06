import kotlinx.datetime.Instant
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import kotlin.test.*

class JsonFileWorkLogStoreTest {
    private val defaultFilePath = "/home/user/workLog.json".toPath()
    private val validFilePath = "/home/user/validWorkLog.json".toPath()
    private val invalidFilePath = "/home/user/invalidWorkLog.json".toPath()
    private val fs = FakeFileSystem()

    @BeforeTest
    fun setUpFileSystem() {
        fs.createDirectories(validFilePath.parent!!)
        fs.write(validFilePath) {
            writeUtf8(
                """
                |[
                |    {
                |        "start": "2023-01-20T08:43:13.546Z",
                |        "end": "2023-01-21T01:13:05.938457Z"
                |    },
                |    {
                |        "start": "2023-02-05T14:53:20.491888Z",
                |        "end": "2023-02-05T14:53:51.650201Z"
                |    }
                |]
            """.trimMargin()
            )
        }
        fs.write(invalidFilePath) {
            writeUtf8(
                """
                |[
                |    {
                |        "start": "2023-01-20T08:43:13.546Z",
                |        "end": "2023-01-21S01:13:05.938457Z"
                |    },
                |    {
                |        "start": "2023-02-05T14:53:20.491888Z",
                |        "end": "2023-02-05T14:53:51.650201Z"
                |    }
                ]
            """.trimMargin()
            )
        }
    }

    @AfterTest
    fun checkNoOpenFiles() {
        fs.checkNoOpenFiles()
    }

    @Test
    fun `When the backing JSON file does not exist, the work log is initially empty`() {
        val store = JsonFileWorkLogStore(filePath = defaultFilePath, fs)
        assertTrue(store.workLog.isEmpty(), "The work log should be empty.")
    }

    @Test
    fun `When the backing JSON file contains valid data, the work log is filled accordingly`() {
        val store = JsonFileWorkLogStore(filePath = validFilePath, fs)
        assertEquals(
            listOf(
                DateTimeInterval(
                    start = Instant.parse("2023-01-20T08:43:13.546Z"),
                    end = Instant.parse("2023-01-21T01:13:05.938457Z")
                ),
                DateTimeInterval(
                    start = Instant.parse("2023-02-05T14:53:20.491888Z"),
                    end = Instant.parse("2023-02-05T14:53:51.650201Z")
                )
            ), store.workLog
        )
    }

    @Test
    fun `When the backing JSON file contains invalid data, an exception is thrown`() {
        assertFails {
            JsonFileWorkLogStore(filePath = invalidFilePath, fs)
        }
    }

    @Test
    fun `Logging work creates a new backing file if one didn't previously exist`() {
        val store = JsonFileWorkLogStore(filePath = defaultFilePath, fs)
        store.logWork(
            DateTimeInterval(
                start = Instant.parse("2023-02-05T15:35:11.123Z"),
                end = Instant.parse("2023-02-05T16:05:21.456Z")
            )
        )
        assertTrue(fs.exists(defaultFilePath), "The backing file should have been created upon logging work.")
        assertEquals("""
            |[
            |    {
            |        "start": "2023-02-05T15:35:11.123Z",
            |        "end": "2023-02-05T16:05:21.456Z"
            |    }
            |]
        """.trimMargin(), fs.read(defaultFilePath) { readUtf8() })
    }

    @Test
    fun `Logging work overwrites the backing file if one previously existed`() {
        val store = JsonFileWorkLogStore(filePath = validFilePath, fs)
        store.logWork(
            DateTimeInterval(
                start = Instant.parse("2023-02-05T14:53:30Z"),
                end = Instant.parse("2023-02-05T14:55:00Z")
            )
        )
        assertEquals("""
            |[
            |    {
            |        "start": "2023-01-20T08:43:13.546Z",
            |        "end": "2023-01-21T01:13:05.938457Z"
            |    },
            |    {
            |        "start": "2023-02-05T14:53:20.491888Z",
            |        "end": "2023-02-05T14:55:00Z"
            |    }
            |]
        """.trimMargin(), fs.read(validFilePath) { readUtf8() }, "Overlapping intervals should be merged")

        store.logWork(
            DateTimeInterval(
                start = Instant.parse("2023-02-05T15:35:11.123Z"),
                end = Instant.parse("2023-02-05T16:05:21.456Z")
            )
        )
        assertEquals("""
            |[
            |    {
            |        "start": "2023-01-20T08:43:13.546Z",
            |        "end": "2023-01-21T01:13:05.938457Z"
            |    },
            |    {
            |        "start": "2023-02-05T14:53:20.491888Z",
            |        "end": "2023-02-05T14:55:00Z"
            |    },
            |    {
            |        "start": "2023-02-05T15:35:11.123Z",
            |        "end": "2023-02-05T16:05:21.456Z"
            |    }
            |]
        """.trimMargin(), fs.read(validFilePath) { readUtf8() }, "Non-overlapping interval is appended")
    }

    @Test
    fun `Clearing the store deletes the backing file`() {
        val store = JsonFileWorkLogStore(filePath = validFilePath, fs)
        store.clear()
        assertFalse(fs.exists(validFilePath), "The backing file should have been deleted.")
    }

    @Test
    fun `Clearing the store when no backing file exists doesn't throw`() {
        val store = JsonFileWorkLogStore(filePath = defaultFilePath, fs)
        store.clear()
        assertFalse(fs.exists(defaultFilePath), "The backing file should still not exist.")
    }

    @Test
    fun `After clearing the store, additional work logs recreate the backing file`() {
        val store = JsonFileWorkLogStore(filePath = validFilePath, fs)
        store.clear()
        store.logWork(
            DateTimeInterval(
                start = Instant.parse("2023-02-05T15:35:11.123Z"),
                end = Instant.parse("2023-02-05T16:05:21.456Z")
            )
        )
        assertTrue(fs.exists(validFilePath), "The backing file should have been recreated.")
    }

}