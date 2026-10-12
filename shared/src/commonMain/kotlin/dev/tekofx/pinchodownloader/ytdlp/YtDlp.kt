package dev.tekofx.pinchodownloader.ytdlp

import dev.tekofx.pinchodownloader.entities.YtDlpPlaylistResult
import dev.tekofx.pinchodownloader.entities.YtDlpPlaylistVideo
import dev.tekofx.pinchodownloader.entities.YtDlpVideoInfoResult

expect suspend fun downloadYtDlp(url: String, outDir: String, onProgress: (Double) -> Unit)


expect suspend fun getVideoInfo(url: String): YtDlpVideoInfoResult


expect suspend fun getPlaylistInfo(url: String): YtDlpPlaylistResult


expect suspend fun getOEmbed(url: String): YtDlpPlaylistVideo?
