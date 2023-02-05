import androidx.compose.runtime.mutableStateListOf

class JsonFileWorkLogStore : WorkLogStore {
    override val workLog = mutableStateListOf<DateTimeInterval>()

    override fun logWork(work: DateTimeInterval) {
        val merged = workLog.lastOrNull()?.merge(work)
        if (merged != null) {
            workLog[workLog.lastIndex] = merged
        } else {
            workLog.add(work)
        }
    }

    override fun clear() {
        workLog.clear()
    }
}