package dev.tekofx.pinchodownloader.entities

import dev.tekofx.pinchodownloader.ytdlp.YtDlpFormat

data class Video(
    val id: Int,
    val title: String,
    val thumbnail: String,
    val url: String,
    val ytDlpFormats: List<YtDlpFormat>,


    var status: TaskStatus = TaskStatus.PENDING,
    var progress: Float = 0f
)


