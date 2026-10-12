package dev.tekofx.pinchodownloader.ytdlp

expect suspend fun downloadYtDlp(url: String, outDir: String, onProgress: (Double) -> Unit)


expect suspend fun fetchVideo(url: String): YtDlpVideoResult


expect suspend fun fetchPlaylist(url: String): YtDlpPlaylistResult


expect suspend fun getOEmbed(url: String): YtDlpPlaylistVideo?


sealed interface YtDlpVideoResult {
    data class Error(val message: String) : YtDlpVideoResult
    data class Success(val ytDlpVideo: YtDlpVideo) : YtDlpVideoResult

    data class AgeRestricted(val ytDlpPlaylistVideo: YtDlpPlaylistVideo) : YtDlpVideoResult
}


sealed interface YtDlpPlaylistResult {
    data class Error(val message: String) : YtDlpPlaylistResult
    data class Success(val ytDlpPlaylist: YtDlpPlaylist) : YtDlpPlaylistResult
}