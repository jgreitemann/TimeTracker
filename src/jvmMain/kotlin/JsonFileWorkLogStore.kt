import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.IOException
import okio.Path.Companion.toPath

class JsonFileWorkLogStore : WorkLogStore {
    override var workLog = mutableStateListOf<DateTimeInterval>()

    private val filePath = "${System.getProperty("user.home")}/workLog.json".toPath()
    private val json = Json { prettyPrint = true }

    init {
        try {
            val fileContent = FileSystem.SYSTEM.read(filePath) {
                readUtf8()
            }
            workLog = json.decodeFromString<List<DateTimeInterval>>(fileContent).toMutableStateList()
        } catch (_: IOException) {
        }
    }

    override fun logWork(work: DateTimeInterval) {
        val merged = workLog.lastOrNull()?.merge(work)
        if (merged != null) {
            workLog[workLog.lastIndex] = merged
        } else {
            workLog.add(work)
        }

        writeToDisk()
    }

    override fun clear() {
        workLog.clear()

        writeToDisk()
    }

    private fun writeToDisk() {
        val fileContent = json.encodeToString(workLog.toList())
        FileSystem.SYSTEM.write(filePath) {
            writeUtf8(fileContent)
        }
    }
}