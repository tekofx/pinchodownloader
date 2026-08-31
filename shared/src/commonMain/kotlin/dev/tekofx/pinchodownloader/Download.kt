package dev.tekofx.pinchodownloader

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File

actual suspend fun downloadYtDlp(url: String, outDir: String, onProgress: (Double) -> Unit) {
    withContext(Dispatchers.IO) {
        val proc = ProcessBuilder(
            "yt-dlp", "-t", "mp4",
            "-o", "$outDir/%(title)s.%(ext)s",
            "--newline",
            "--progress-template", "download:%(progress._percent_str)s",
            url
        ).redirectErrorStream(true).start()

        proc.inputStream.bufferedReader().forEachLine { line ->
            val pct = line.removePrefix("download:").trim().removeSuffix("%")
            pct.toDoubleOrNull()?.let { onProgress(it / 100.0) }
        }
        proc.waitFor()
    }
}

actual fun getDownloadsDir(): String {
    val home = System.getProperty("user.home")
    val dir = File(home, "Downloads")
    if (!dir.exists()) dir.mkdirs()
    return dir.absolutePath
}

actual suspend fun getVideoInfo(url: String): Pair<String, String> {
    return withContext(Dispatchers.IO) {
        val proc = ProcessBuilder("yt-dlp", "-J", url)
            .redirectErrorStream(true).start()

        val json = proc.inputStream.bufferedReader().readText()
        proc.waitFor()

        // Parse with kotlinx.serialization or org.json
        val obj = Json.parseToJsonElement (json).jsonObject
        val title = obj["title"]!!.jsonPrimitive.content
        val thumbnail = obj["thumbnail"]!!.jsonPrimitive.content
        title to thumbnail
    }
}