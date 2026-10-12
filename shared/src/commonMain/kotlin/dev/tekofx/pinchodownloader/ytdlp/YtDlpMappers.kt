package dev.tekofx.pinchodownloader.ytdlp

import dev.tekofx.pinchodownloader.entities.TaskStatus
import dev.tekofx.pinchodownloader.entities.Video

fun YtDlpVideo.toVideo(): Video {
    // Filter formats with height and formatNote
    val videoFormats = this.ytDlpFormats.filter { it.height != null && it.formatNote != "" }

    // Get FullHd format
    val defaultFormat = videoFormats.firstOrNull { it.formatId == "299" }
    // If not exists get the bigger format
        ?: videoFormats.sortedByDescending { it.height }.firstOrNull()
        ?: this.ytDlpFormats.lastOrNull()

    return Video(
        id = id,
        url = url,
        title = title,
        thumbnail = thumbnails[0].url,
        ytDlpFormats = ytDlpFormats,
        selectedFormat = defaultFormat,
        status = TaskStatus.PENDING,
        progress = 0f,
    )
}

fun YtVideoOembed.toVideo(id: String?, url: String): Video {
    return Video(
        id = id ?: "",
        title = title,
        thumbnail = thumbnail,
        url = url,
        ytDlpFormats = emptyList(),
        selectedFormat = null,
        status = TaskStatus.AGE_RESTRICTED,
        progress = 0f,
    )
}

