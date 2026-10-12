package dev.tekofx.pinchodownloader.entities.states

import dev.tekofx.pinchodownloader.entities.Video
import dev.tekofx.pinchodownloader.ytdlp.YtDlpPlaylist

data class DownloaderState(
    val videos: List<Video> = emptyList(),
    val progress: Float = 0f,
    val latestDownloadVideoIndex: Int = 0,
    val status: String? = null,
    val downloading: Boolean = false,
    val url: String = "",
    val urlIsValid: Boolean = true,
    val showLogs: Boolean = false,

    // Loader
    val loading: Boolean = false,
    val loaderMessage: String = "",

    // Playlist management
    val showPlaylistPopup: Boolean = false,
    val playlistPopupText: String = "",
    val tempPlaylist: YtDlpPlaylist? = null,
)