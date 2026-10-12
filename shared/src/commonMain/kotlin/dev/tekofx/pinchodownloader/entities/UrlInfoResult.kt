package dev.tekofx.pinchodownloader.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class VideoInfo(
    val id: String,
    val title: String,
    val thumbnail: String,
    @SerialName("webpage_url") val url: String,
    val formats: List<Format>
)

@Serializable
data class Format(
    @SerialName("format_id") val formatId: String,
    @SerialName("format_note") val formatNote: String? = null,
)


data class MinimalVideoInfo(val title: String, val url: String, val thumbnail: String)

data class PlaylistInfo(val url: String, val videos: List<MinimalVideoInfo>)

sealed class VideoInfoResult {
    data class Error(val message: String) : VideoInfoResult()
    data class Success(val videoInfo: VideoInfo) : VideoInfoResult()

    data class AgeRestricted(val minimalVideoInfo: MinimalVideoInfo) : VideoInfoResult()
}


sealed class PlaylistInfoResult {
    data class Error(val message: String) : PlaylistInfoResult()
    data class Success(val playlistInfo: PlaylistInfo) : PlaylistInfoResult()
}