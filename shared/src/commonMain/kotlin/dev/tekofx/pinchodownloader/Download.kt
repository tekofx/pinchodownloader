package dev.tekofx.pinchodownloader

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File

expect suspend fun downloadYtDlp(url: String, outDir: String, onProgress: (Double) -> Unit)

expect fun getDownloadsDir(): String


expect suspend fun getVideoInfo(url: String): Pair<String, String>