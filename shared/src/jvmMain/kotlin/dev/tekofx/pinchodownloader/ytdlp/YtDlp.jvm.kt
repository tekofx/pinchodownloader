package dev.tekofx.pinchodownloader.ytdlp

import dev.tekofx.pinchodownloader.entities.*
import dev.tekofx.pinchodownloader.log.LogStatus
import dev.tekofx.pinchodownloader.log.LogStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*

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
            val process = ProcessBuilder("yt-dlp", "-J", url)
                .redirectErrorStream(false).start()
            val json = process.inputStream.bufferedReader().readText()
            val exitCode = process.waitFor()

            if (exitCode != 0) {
                val stderr = process.errorStream.bufferedReader().readText()

                if (stderr.contains("Sign in to confirm your age")) {
                    val minimalVideoInfo = getOEmbed(url)
                    if (minimalVideoInfo != null) {
                        return@withContext VideoInfoResult.AgeRestricted(minimalVideoInfo)
                    }

                }
                return@withContext VideoInfoResult.Error("yt-dlp failed: ${stderr.take(200)}")

            }

            val videoInfo = parseVideoInfo(json)

            LogStore.debug("Parse", videoInfo.toString())

            VideoInfoResult.Success(videoInfo)
        } catch (e: Exception) {
            LogStore.error("getVideoInfo", e.message.toString())
            VideoInfoResult.Error(e.message ?: "Unknown error")
        }

    }
}

fun parseVideoInfo(jsonString: String): VideoInfo {
    val jsonConfig = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    return jsonConfig.decodeFromString<VideoInfo>(jsonString)
}

actual suspend fun getOEmbed(url: String): MinimalVideoInfo? {
    val videoId = extractVideoId(url)
    return withContext(Dispatchers.IO) {
        val url = "https://www.youtube.com/oembed?url=https://www.youtube.com/watch?v=$videoId&format=json"
        val json = java.net.URL(url).readText()
        val obj = Json.parseToJsonElement(json) as? JsonObject ?: return@withContext null

        MinimalVideoInfo(
            title = obj.getString("title") ?: "",
            thumbnail = obj.getString("thumbnail_url") ?: "",
            url = "https://www.youtube.com/watch?v=$videoId",
        )
    }
}


actual suspend fun getPlaylistInfo(url: String): PlaylistInfoResult {
    return withContext(Dispatchers.IO) {

        val process = ProcessBuilder("yt-dlp", "--flat-playlist", "-J", url)
            .redirectErrorStream(false).start()
        val json = process.inputStream.bufferedReader().readText()
        process.waitFor()

        val root = Json.parseToJsonElement(json).jsonObject

        val entries = root["entries"]?.jsonArray


        val videos: MutableList<MinimalVideoInfo> = mutableListOf()

        entries?.forEach { entry ->
            val json = entry.jsonObject
            val title = json["title"]?.jsonPrimitive?.content
                ?: return@withContext PlaylistInfoResult.Error("No title in response")
            val videoUrl = json["webpage_url"]?.jsonPrimitive?.content
                ?: "https://www.youtube.com/watch?v=${json["id"]?.jsonPrimitive?.content}"
            val thumbnail = getThumbnail(json) ?: ""
            videos.addLast(MinimalVideoInfo(title, videoUrl, thumbnail))
        }

        return@withContext PlaylistInfoResult.Success(PlaylistInfo(url, videos))

    }
}


private fun getAllFormats(jsonString: String): List<Format> {
    val jsonConfig = Json { ignoreUnknownKeys = true } // Ignora los campos que no mapeamos
    val format = jsonConfig.decodeFromString<List<Format>>(jsonString)
    return format
}

private fun JsonObject.getString(key: String): String? =
    (this[key] as? JsonPrimitive)?.contentOrNull

private fun getThumbnail(entry: JsonObject): String? {
    entry.getString("thumbnail")?.let { return it }  // null here (flat playlist)

    val arr = entry["thumbnails"] as? JsonArray ?: return null
    return arr
        .filterIsInstance<JsonObject>()
        .maxByOrNull { (it["width"] as? JsonPrimitive)?.intOrNull ?: 0 }
        ?.getString("url")
}


private fun extractVideoId(url: String): String? {
    // /watch?v=VIDEO_ID
    url.takeIf { it.contains("/watch?v=") }?.let {
        return it.substringAfter("v=").substringBefore("&")
    }

    // /shorts/VIDEO_ID
    url.takeIf { it.contains("/shorts/") }?.let {
        return it.substringAfter("/shorts/").substringBefore("?").substringBefore("/")
    }

    // /embed/VIDEO_ID
    url.takeIf { it.contains("/embed/") }?.let {
        return it.substringAfter("/embed/").substringBefore("?").substringBefore("/")
    }

    // /live/VIDEO_ID
    url.takeIf { it.contains("/live/") }?.let {
        return it.substringAfter("/live/").substringBefore("?").substringBefore("/")
    }

    // youtu.be/VIDEO_ID
    url.takeIf { it.contains("youtu.be/") }?.let {
        return it.substringAfter("youtu.be/").substringBefore("?").substringBefore("/")
    }

    return null
}