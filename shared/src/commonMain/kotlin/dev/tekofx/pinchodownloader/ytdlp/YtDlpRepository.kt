package dev.tekofx.pinchodownloader.ytdlp

import dev.tekofx.pinchodownloader.entities.Video

expect suspend fun downloadYtDlp(url: String, format: YtDlpFormat?, onProgress: (Double) -> Unit)


expect suspend fun fetchVideo(url: String): YtDlpVideoResult


expect suspend fun fetchPlaylist(url: String): YtDlpPlaylistResult


expect suspend fun fetchOembed(url: String): Video


sealed interface YtDlpVideoResult {
    data class Error(val message: String) : YtDlpVideoResult
    data class Success(val video: Video) : YtDlpVideoResult

    data class AgeRestricted(val video: Video) : YtDlpVideoResult
}


sealed interface YtDlpPlaylistResult {
    data class Error(val message: String) : YtDlpPlaylistResult
    data class Success(val ytDlpPlaylist: YtDlpPlaylist) : YtDlpPlaylistResult
}