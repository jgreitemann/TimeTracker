import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.IOException
import okio.Path
import okio.Path.Companion.toPath

class JsonFileWorkLogStore(
    val filePath: Path = "${System.getProperty("user.home")}/workLog.json".toPath(),
    private var fs: FileSystem = FileSystem.SYSTEM
) : WorkLogStore {
    override var workLog = mutableStateListOf<DateTimeInterval>()
    private val json = Json { prettyPrint = true }

    init {
        try {
            val fileContent = fs.read(filePath) { readUtf8() }
            workLog = json.decodeFromString<List<DateTimeInterval>>(fileContent).toMutableStateList()
        } catch (_: IOException) {
        }
    }

    override fun update(old: DateTimeInterval?, new: DateTimeInterval?) {
        workLog.remove(old)

        if (new != null) {
            val toBeAdded = run {
                for (existing in workLog) {
                    val merged = existing merge new
                    if (merged != null) {
                        workLog.remove(existing)
                        return@run merged
                    }
                }
                new
            }
            workLog.add(toBeAdded)
        }

        workLog.sortBy { it.start }

        val fileContent = json.encodeToString(workLog.toList())
        fs.write(filePath) { writeUtf8(fileContent) }
    }

    override fun clear() {
        workLog.clear()
        fs.delete(filePath)
    }
}