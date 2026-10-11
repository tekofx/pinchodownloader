package dev.tekofx.pinchodownloader.ytdlp

import dev.tekofx.pinchodownloader.entities.UrlInfoResult

expect suspend fun downloadYtDlp(url: String, outDir: String, onProgress: (Double) -> Unit)


expect suspend fun getUrlInfo(url: String): UrlInfoResult