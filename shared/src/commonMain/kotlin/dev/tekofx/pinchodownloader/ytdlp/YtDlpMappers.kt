package dev.tekofx.pinchodownloader.ytdlp

import dev.tekofx.pinchodownloader.entities.TaskStatus
import dev.tekofx.pinchodownloader.entities.Video
import dev.tekofx.pinchodownloader.entities.VideoFormat


fun YtDlpFormat.toFormat(): VideoFormat {
    return VideoFormat(
        formatId = formatId,
        name = "${height}p (${videoExtension})"
    )
}


fun List<YtDlpFormat>.toFormatList(): List<VideoFormat> {
    return map { it.toFormat() }
}


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
        formats = ytDlpFormats.toFormatList(),
        selectedFormat = defaultFormat?.toFormat(),
        status = TaskStatus.PENDING,
        progress = 0f,
    )
}

fun YtVideoOembed.toYtDlpVideo(id: String?, url: String): YtDlpVideo {
    return YtDlpVideo(
        id = id ?: "",
        title = title,
        thumbnails = listOf(YtDlpThumbnail(thumbnail)),
        url = url,
        ytDlpFormats = emptyList(),
    )
}

