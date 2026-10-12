package dev.tekofx.pinchodownloader.entities

data class Video(
    val id: String,
    val title: String,
    val thumbnail: String,
    val url: String,
    val formats: List<VideoFormat>,
    val selectedFormat: VideoFormat?,

    var status: TaskStatus = TaskStatus.PENDING,
    var progress: Float = 0f
)


