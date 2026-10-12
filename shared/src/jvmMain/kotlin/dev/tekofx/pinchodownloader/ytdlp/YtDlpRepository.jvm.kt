package dev.tekofx.pinchodownloader.ytdlp

import dev.tekofx.pinchodownloader.entities.VideoFormat
import dev.tekofx.pinchodownloader.filesystem.getDownloadsDir
import dev.tekofx.pinchodownloader.log.LogStatus
import dev.tekofx.pinchodownloader.log.LogStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.*

actual suspend fun downloadYtDlp(url: String, format: VideoFormat?, onProgress: (Double) -> Unit) {
    withContext(Dispatchers.IO) {

        val ytDlpCommand = buildList {
            add("yt-dlp")

            add("-f")
            if (format == null) {
                add("bestvideo+bestaudio/best")
            } else {
                add("${format.formatId}+bestaudio/best")
            }

            add("-o")
            add("${getDownloadsDir()}/%(title)s.%(ext)s")

            add("--newline")
            add("--progress-template")
            add("download:%(progress._percent_str)s")

            add("--extractor-args")
            add("youtube:player-client=web_embedded,web,tv")

            add("--remote-components")
            add("ejs:github")

            add(url)
        }

        val proc = ProcessBuilder(
            ytDlpCommand
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
actual suspend fun fetchVideo(url: String): YtDlpVideoResult {
    return withContext(Dispatchers.IO) {

        try {
            val process = ProcessBuilder("yt-dlp", "-J", url)
                .redirectErrorStream(false).start()
            val json = process.inputStream.bufferedReader().readText()
            val exitCode = process.waitFor()

            if (exitCode != 0) {
                val stderr = process.errorStream.bufferedReader().readText()

                if (stderr.contains("Sign in to confirm your age")) {
                    val minimalVideoInfo = fetchOembed(url)
                    return@withContext YtDlpVideoResult.AgeRestricted(minimalVideoInfo)

                }
                return@withContext YtDlpVideoResult.Error("yt-dlp failed: ${stderr.take(200)}")

            }
            val ytDlpVideo = parseYtDlpVideo(json)
            YtDlpVideoResult.Success(ytDlpVideo)
        } catch (e: JsonDecodingException) {
            LogStore.error("getVideoInfo", e.toString())
            YtDlpVideoResult.Error(e.message ?: "Unknown error")

        } catch (e: SerializationException) {
            // Captura cualquier otro error general de la librería de serialización
            LogStore.error("Serialization Error", "No se pudo mapear el JSON a las entidades: ${e.message}")
            YtDlpVideoResult.Error(e.message ?: "Unknown error")
        } catch (e: Exception) {
            LogStore.error("getVideoInfo", e.toString())
            YtDlpVideoResult.Error(e.message ?: "Unknown error")
        }
    }

}

fun parseYtDlpVideo(jsonString: String): YtDlpVideo {
    val jsonConfig = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    return jsonConfig.decodeFromString<YtDlpVideo>(jsonString)
}

fun parsePlaylistInfo(jsonString: String): YtDlpPlaylist {
    val jsonConfig = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    return jsonConfig.decodeFromString<YtDlpPlaylist>(jsonString)
}

fun parseOembed(jsonString: String): YtVideoOembed {
    val jsonConfig = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    return jsonConfig.decodeFromString<YtVideoOembed>(jsonString)
}

actual suspend fun fetchOembed(url: String): YtDlpVideo {
    val videoId = extractVideoId(url)
    return withContext(Dispatchers.IO) {
        val url = "https://www.youtube.com/oembed?url=https://www.youtube.com/watch?v=$videoId&format=json"
        val json = java.net.URL(url).readText()
        val ytVideoOembed = parseOembed(json)

        return@withContext ytVideoOembed.toYtDlpVideo(id = videoId, url = url)


    }
}


actual suspend fun fetchPlaylist(url: String): YtDlpPlaylistResult {
    return withContext(Dispatchers.IO) {

        val process = ProcessBuilder("yt-dlp", "--flat-playlist", "-J", url)
            .redirectErrorStream(false).start()
        val json = process.inputStream.bufferedReader().readText()
        process.waitFor()

        val ytDlpPlaylist = parsePlaylistInfo(json)
        return@withContext YtDlpPlaylistResult.Success(ytDlpPlaylist)
    }
}


private fun getAllFormats(jsonString: String): List<YtDlpFormat> {
    val jsonConfig = Json { ignoreUnknownKeys = true } // Ignora los campos que no mapeamos
    val ytDlpFormat = jsonConfig.decodeFromString<List<YtDlpFormat>>(jsonString)
    return ytDlpFormat
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