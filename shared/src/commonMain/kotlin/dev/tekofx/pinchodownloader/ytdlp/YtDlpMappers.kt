package dev.tekofx.pinchodownloader.ytdlp

import dev.tekofx.pinchodownloader.entities.TaskStatus
import dev.tekofx.pinchodownloader.entities.Video

fun YtDlpVideo.toVideo(): Video {
    return Video(
        id = id,
        url = url,
        title = title,
        thumbnail = thumbnails[0].url,
        ytDlpFormats = ytDlpFormats,
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
        status = TaskStatus.AGE_RESTRICTED,
        progress = 0f,
    )
}

