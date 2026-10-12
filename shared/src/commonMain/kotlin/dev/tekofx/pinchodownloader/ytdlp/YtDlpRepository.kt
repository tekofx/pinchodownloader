package dev.tekofx.pinchodownloader.ytdlp

import dev.tekofx.pinchodownloader.entities.VideoFormat

expect suspend fun downloadYtDlp(url: String, format: VideoFormat?, onProgress: (Double) -> Unit)


expect suspend fun fetchVideo(url: String): YtDlpVideoResult


expect suspend fun fetchPlaylist(url: String): YtDlpPlaylistResult


expect suspend fun fetchOembed(url: String): YtDlpVideo


sealed interface YtDlpVideoResult {
    data class Error(val message: String) : YtDlpVideoResult
    data class Success(val video: YtDlpVideo) : YtDlpVideoResult

    data class AgeRestricted(val video: YtDlpVideo) : YtDlpVideoResult
}


sealed interface YtDlpPlaylistResult {
    data class Error(val message: String) : YtDlpPlaylistResult
    data class Success(val ytDlpPlaylist: YtDlpPlaylist) : YtDlpPlaylistResult
}