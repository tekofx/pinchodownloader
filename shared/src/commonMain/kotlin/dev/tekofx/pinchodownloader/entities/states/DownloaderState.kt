package dev.tekofx.pinchodownloader.entities.states

import dev.tekofx.pinchodownloader.entities.Video

data class DownloaderState(
    val videos: List<Video> = emptyList(),
    val progress: Float = 0f,
    val latestDownloadVideoIndex: Int = 0,
    val status: String? = null,
    val loading: Boolean = false,
    val downloading: Boolean = false,
    val url: String = "",
    val urlIsValid: Boolean = true,
    val showLogs: Boolean = false
)