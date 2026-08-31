package dev.tekofx.pinchodownloader

import dev.tekofx.pinchodownloader.entities.GitHubRelease
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.URI

suspend fun getLatestYtDlpGithubRelease(): String? = withContext(Dispatchers.IO) {
    try {
        val url = URI.create("https://api.github.com/repos/yt-dlp/yt-dlp/releases/latest").toURL()
        val response = url.readText()
        val release = Json { ignoreUnknownKeys = true }.decodeFromString<GitHubRelease>(response)
        release.tag_name
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


suspend fun getInstalledYtDlpVersion(): String? = withContext(Dispatchers.IO) {
    try {
        val process = ProcessBuilder("yt-dlp", "--version").start()
        val version = process.inputStream.bufferedReader().readLine()?.trim()
        process.waitFor()
        version
    } catch (e: Exception) {
        null
    }
}

suspend fun checkYtDlpUpdate(): Boolean {
    val installed = getInstalledYtDlpVersion() ?: return false
    val latest = getLatestYtDlpGithubRelease() // from previous answer
    return installed != latest
}

suspend fun getLatestPinchoDownloaderGithubVersion(): String? = withContext(Dispatchers.IO) {
    try {
        val url = URI.create("https://api.github.com/repos/tekofx/pinchodownloader/releases/latest").toURL()
        val response = url.readText()
        val release = Json { ignoreUnknownKeys = true }.decodeFromString<GitHubRelease>(response)
        release.tag_name
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

suspend fun checkPinchoDownloaderUpdate(): Boolean {
    val latest = getLatestPinchoDownloaderGithubVersion() // from previous answer
    return installed != latest
}