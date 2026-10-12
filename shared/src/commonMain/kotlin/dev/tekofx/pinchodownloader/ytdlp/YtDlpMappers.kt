package dev.tekofx.pinchodownloader.ytdlp

import dev.tekofx.pinchodownloader.entities.TaskStatus
import dev.tekofx.pinchodownloader.entities.Video
import dev.tekofx.pinchodownloader.entities.VideoFormat


fun YtDlpFormat.toFormat(): VideoFormat {
    return VideoFormat(
        formatId = formatId,
        name = "${height}p"
    )
}


fun List<YtDlpFormat>.toFormatList(): List<VideoFormat> {
    return map { it.toFormat() }
}


fun YtDlpVideo.toVideo(): Video {
    val validVideoFormats = this.ytDlpFormats.filter {
        it.height != null && !it.formatNote.isNullOrBlank() && it.vcodec != "none"
    }

    // Group by height and we get the best format for each height
    val uniqueFormatsPerResolution = validVideoFormats.groupBy { it.height }
        .mapValues { (_, formatsInResolution) ->
            // Sort to find best format
            formatsInResolution.minByOrNull { format ->
                val codec = format.vcodec ?: ""
                val codecScore = when {
                    codec.contains("vp9", ignoreCase = true) -> 1
                    codec.contains("av01", ignoreCase = true) -> 2
                    codec.contains("avc1", ignoreCase = true) -> 3
                    else -> 4
                }
                val fpsValue = format.fps?.toInt() ?: 0
                codecScore * 1000 - fpsValue
            }
        }
        .values
        .filterNotNull()
        .sortedByDescending { it.height }

    // Get 1080p as default format
    val defaultFormat = uniqueFormatsPerResolution.firstOrNull { it.height == 1080 }
        ?: uniqueFormatsPerResolution.firstOrNull()
        ?: this.ytDlpFormats.lastOrNull()

    return Video(
        id = id,
        url = url,
        title = title,
        thumbnail = thumbnails.firstOrNull()?.url ?: "",
        formats = uniqueFormatsPerResolution.toFormatList(),
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

