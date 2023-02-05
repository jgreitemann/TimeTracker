import androidx.compose.runtime.mutableStateListOf

class JsonFileWorkLogStore : WorkLogStore {
    override val workLog = mutableStateListOf<DateTimeInterval>()
}