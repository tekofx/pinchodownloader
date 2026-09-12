package dev.tekofx.pinchodownloader

import dev.tekofx.pinchodownloader.entities.GitHubRelease
import dev.tekofx.pinchodownloader.log.LogStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.URI

private val json = Json { ignoreUnknownKeys = true }

suspend fun getLatestYtDlpGithubRelease(): String? = withContext(Dispatchers.IO) {
    try {
        val url = URI.create("https://api.github.com/repos/yt-dlp/yt-dlp/releases/latest").toURL()
        val response = url.readText()
        val release = json.decodeFromString<GitHubRelease>(response)
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
        LogStore.error("Get Installed YtDlp Version", e.stackTraceToString())
        null
    }
}

suspend fun checkYtDlpUpdate(): Boolean {
    val installed = getInstalledYtDlpVersion() ?: return false
    LogStore.debug("checkYtDlpUpdate", "Yt-Dlp current version: $installed")
    val latest = getLatestYtDlpGithubRelease()
    LogStore.debug("checkYtDlpUpdate", "Yt-dlp Github Version: $latest")
    return installed != latest
}

suspend fun getLatestPinchoDownloaderGithubVersion(): String? = withContext(Dispatchers.IO) {
    try {
        val url = URI.create("https://api.github.com/repos/tekofx/pinchodownloader/releases/latest").toURL()
        val response = url.readText()
        val release = json.decodeFromString<GitHubRelease>(response)
        release.tag_name
    } catch (e: Exception) {
        LogStore.error("getLatestPinchoDownloaderGithubVersion", e.stackTraceToString())
        null
    }
}

fun getAppVersion(): String {
    return System.getProperty("jpackage.app-version") ?: "Development Version"
}

suspend fun checkPinchoDownloaderUpdate(): Boolean {
    val latest = getLatestPinchoDownloaderGithubVersion() // from previous answer
    LogStore.debug("checkPinchoDownloaderUpdate", "App github version: $latest")

    val currentVersion = getAppVersion()
    LogStore.debug("checkPinchoDownloaderUpdate", "App current version: $currentVersion")

    return currentVersion != latest
}