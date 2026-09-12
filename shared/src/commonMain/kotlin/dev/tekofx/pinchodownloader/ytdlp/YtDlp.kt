package dev.tekofx.pinchodownloader.ytdlp

import dev.tekofx.pinchodownloader.entities.VideoInfoResult

expect suspend fun downloadYtDlp(url: String, outDir: String, onProgress: (Double) -> Unit)


expect suspend fun getVideoInfo(url: String): VideoInfoResult