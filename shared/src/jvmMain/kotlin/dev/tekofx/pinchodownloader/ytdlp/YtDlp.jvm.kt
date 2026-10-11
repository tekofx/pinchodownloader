package dev.tekofx.pinchodownloader.ytdlp

import dev.tekofx.pinchodownloader.entities.UrlInfoResult
import dev.tekofx.pinchodownloader.entities.VideoInfo
import dev.tekofx.pinchodownloader.log.LogStatus
import dev.tekofx.pinchodownloader.log.LogStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.*
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


@OptIn(ExperimentalSerializationApi::class)
actual suspend fun getUrlInfo(url: String): UrlInfoResult {
    return withContext(Dispatchers.IO) {
        try {
            val entries = getEntries(url)
            val videos = mutableListOf<VideoInfo>()

            for (entry in entries) {
                LogStore.debug("getUrlInfo", "entry: $entry")
                val title = entry["title"]?.jsonPrimitive?.content
                    ?: return@withContext UrlInfoResult.Error("No title in response")
                val thumbnail = getThumbnail(entry)
                    ?: return@withContext UrlInfoResult.Error("No thumbnail in response")

                val formatNote = entry["format_note"]?.jsonPrimitive?.content?.substringBefore("+")
                    ?: return@withContext UrlInfoResult.Error("No format note in response")

                val videoUrl = entry["webpage_url"]?.jsonPrimitive?.content
                    ?: "https://www.youtube.com/watch?v=${entry["id"]?.jsonPrimitive?.content}"

                videos.addLast(VideoInfo(title, thumbnail, videoUrl, formatNote))
            }

            if (videos.size > 1) {
                UrlInfoResult.Playlist(videos)
            } else
                UrlInfoResult.SingleVideo(videos.first())


        } catch (e: IOException) {
            LogStore.error("yt-dlp", e.message ?: "IO error")
            return@withContext UrlInfoResult.Error("yt-dlp not found: ${e.message}")
        } catch (e: JsonException) {
            LogStore.error("yt-dlp", "JSON parse error: ${e.message}")
            return@withContext UrlInfoResult.Error("Invalid JSON from yt-dlp")
        } catch (e: Exception) {
            LogStore.error("yt-dlp", e.message ?: "unexpected")
            return@withContext UrlInfoResult.Error("Unexpected error: ${e.message}")
        }


    }
}


fun getEntries(url: String): List<JsonObject> {
    val process = ProcessBuilder("yt-dlp", "-J", url)
        .redirectErrorStream(false).start()
    val json = process.inputStream.bufferedReader().readText()
    process.waitFor()

    val root = Json.parseToJsonElement(json).jsonObject

    return (root["entries"] as? JsonArray)
        ?.filterIsInstance<JsonObject>()   // ← drops JsonNull entries
        ?: listOf(root)
}

fun getThumbnail(entry: JsonObject): String? {
    // 1. Direct string (resolved)
    entry["thumbnail"]?.jsonPrimitive?.content?.let { return it }

    // 2. Array of thumbnail objects — pick the largest
    val arr = entry["thumbnails"] as? JsonArray ?: return null
    return arr
        .filterIsInstance<JsonObject>()
        .maxByOrNull { (it["width"]?.jsonPrimitive?.int ?: 0) }
        ?.get("url")?.jsonPrimitive?.content
}