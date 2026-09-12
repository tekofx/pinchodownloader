package dev.tekofx.pinchodownloader.log

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

enum class LogStatus { DEBUG, INFO, WARN, ERROR, FATAL }

data class LogEntry(
    val time: String,
    val tag: String,
    val message: String,
    val status: LogStatus = LogStatus.DEBUG
)

object LogStore {
    private val _logs = MutableStateFlow<List<LogEntry>>(emptyList())
    val logs: StateFlow<List<LogEntry>> = _logs

    fun log(tag: String, message: String, status: LogStatus = LogStatus.DEBUG) {
        val now = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
        val time = now.format(
            LocalDateTime.Format {
                hour(); char(':'); minute(); char(':'); second()
            }
        )

        val line = "[$time] [$tag] [$status] $message"
        when (status) {
            LogStatus.ERROR, LogStatus.FATAL -> System.err.println(line)
            else -> println(line)
        }

        _logs.value += LogEntry(time, tag, message, status)
    }

    fun debug(tag: String, message: String) {
        log(tag, message, LogStatus.DEBUG)
    }

    fun info(tag: String, message: String) {
        log(tag, message, LogStatus.INFO)
    }

    fun warn(tag: String, message: String) {
        log(tag, message, LogStatus.WARN)
    }

    fun error(tag: String, message: String) {
        log(tag, message, LogStatus.ERROR)
    }

    fun fatal(tag: String, message: String) {
        log(tag, message, LogStatus.FATAL)
    }


    fun clear() {
        _logs.value = emptyList()
    }
}