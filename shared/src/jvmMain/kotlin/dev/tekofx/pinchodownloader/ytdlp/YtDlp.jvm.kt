package dev.tekofx.pinchodownloader.ytdlp

import dev.tekofx.pinchodownloader.entities.VideoInfoResult
import dev.tekofx.pinchodownloader.log.LogStatus
import dev.tekofx.pinchodownloader.log.LogStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.IOException

actual suspend fun downloadYtDlp(url: String, outDir: String, onProgress: (Double) -> Unit) {
    withContext(Dispatchers.IO) {
        val proc = ProcessBuilder(
            "yt-dlp", "-t", "mp4",
            "-o", "$outDir/%(title)s.%(ext)s",
            "--newline",
            "--progress-template", "download:%(progress._percent_str)s",
            "--extractor-args", "youtube:player-client=web_embedded,web,tv",
            "--remote-components", "ejs:github",
            url
        ).redirectErrorStream(true).start()

        proc.inputStream.bufferedReader().forEachLine { line ->
            val status = when {
                line.contains("ERROR:") || line.contains("[error]") -> LogStatus.ERROR
                line.contains("WARNING:") || line.contains("[warning]") -> LogStatus.WARN
                line.startsWith("download:") -> LogStatus.DEBUG
                else -> LogStatus.INFO
            }
            LogStore.log(tag = "YT-Dlp Download", message = line, status = status)

            val pct = line.removePrefix("download:").trim().removeSuffix("%")
            pct.toDoubleOrNull()?.let { onProgress(it / 100.0) }
        }
        proc.waitFor()
        val exitCode = proc.waitFor()
        if (exitCode != 0) {
            throw RuntimeException("yt-dlp exited with code $exitCode")
        }
    }
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
                LogStore.error("yt-dlp Get Video Info", "exit=$exitCode")
                LogStore.error("yt-dlp Get Video Info", json.take(500))
                return@withContext VideoInfoResult.Error(
                    "yt-dlp failed (exit $exitCode): ${json.take(200)}"
                )
            }

            val obj = Json.parseToJsonElement(json).jsonObject
            val title = obj["title"]?.jsonPrimitive?.content
                ?: return@withContext VideoInfoResult.Error("No title in response")
            val thumbnail = obj["thumbnail"]?.jsonPrimitive?.content
                ?: return@withContext VideoInfoResult.Error("No thumbnail in response")

            val formatNote = obj["format_note"]?.jsonPrimitive?.content?.substringBefore("+")
                ?: return@withContext VideoInfoResult.Error("No format note in response")

            VideoInfoResult.Success(title, thumbnail, url, formatNote)

        } catch (e: IOException) {
            VideoInfoResult.Error("yt-dlp not found: ${e.message}")
        } catch (e: Exception) {
            VideoInfoResult.Error("Unexpected error: ${e.message}")
        }
    }
}