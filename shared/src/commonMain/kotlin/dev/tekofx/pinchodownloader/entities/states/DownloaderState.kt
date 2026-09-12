package dev.tekofx.pinchodownloader.entities.states

import dev.tekofx.pinchodownloader.entities.Video

data class DownloaderState(
    val videos: List<Video> = emptyList(),
    val progress: Float = 0f,
    val status: String? = null,
    val loading: Boolean = false,
    val downloading: Boolean = false,
    val url: String = "",
    val showLogs: Boolean = false
)