package dev.tekofx.pinchodownloader

import dev.tekofx.pinchodownloader.entities.VideoInfoResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import java.io.IOException

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

actual suspend fun getVideoInfo(url: String): VideoInfoResult {
    return withContext(Dispatchers.IO) {
        try {
            val proc = ProcessBuilder("yt-dlp", "--no-warning", "-J", url)
                .redirectErrorStream(true)
                .start()

            val json = proc.inputStream.bufferedReader().readText()
            val exitCode = proc.waitFor()

            if (exitCode != 0) {
                // yt-dlp prints its error to stdout when redirectErrorStream(true)
                return@withContext VideoInfoResult.Error(
                    "yt-dlp failed (exit $exitCode): ${json.take(200)}"
                )
            }

            val obj = Json.parseToJsonElement(json).jsonObject
            val title = obj["title"]?.jsonPrimitive?.content
                ?: return@withContext VideoInfoResult.Error("No title in response")
            val thumbnail = obj["thumbnail"]?.jsonPrimitive?.content
                ?: return@withContext VideoInfoResult.Error("No thumbnail in response")

            VideoInfoResult.Success(title, thumbnail, url)

        } catch (e: IOException) {
            VideoInfoResult.Error("yt-dlp not found: ${e.message}")
        } catch (e: Exception) {
            VideoInfoResult.Error("Unexpected error: ${e.message}")
        }
    }
}