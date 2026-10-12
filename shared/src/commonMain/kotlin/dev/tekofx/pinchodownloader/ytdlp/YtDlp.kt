package dev.tekofx.pinchodownloader.ytdlp

import dev.tekofx.pinchodownloader.entities.MinimalVideoInfo
import dev.tekofx.pinchodownloader.entities.PlaylistInfoResult
import dev.tekofx.pinchodownloader.entities.VideoInfoResult

expect suspend fun downloadYtDlp(url: String, outDir: String, onProgress: (Double) -> Unit)


expect suspend fun getVideoInfo(url: String): VideoInfoResult


expect suspend fun getPlaylistInfo(url: String): PlaylistInfoResult


expect suspend fun getOEmbed(url: String): MinimalVideoInfo?
