package dev.tekofx.pinchodownloader.entities

data class Video(
    val id: Int,
    val title: String,
    val thumbnail: String,
    var status: TaskStatus = TaskStatus.PENDING,
    var progress: Float = 0f
)

