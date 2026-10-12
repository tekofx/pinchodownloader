package dev.tekofx.pinchodownloader.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class YtDlpVideo(
    val id: String,
    val title: String,
    val thumbnails: List<YtDlpThumbnail>,
    @SerialName("webpage_url")
    val url: String,
    @SerialName("formats")
    val ytDlpFormats: List<YtDlpFormat>
)

@Serializable
data class YtDlpFormat(
    @SerialName("format_id")
    val formatId: String,
    @SerialName("format_note")
    val formatNote: String? = null,
)


@Serializable
data class YtDlpThumbnail(
    val url: String,
)

@Serializable
data class YtDlpPlaylistVideo(
    val id: String,
    val title: String,
    val url: String,
    val thumbnails: List<YtDlpThumbnail>,
)

@Serializable
data class YtDlpPlaylist(
    val id: String,
    val title: String,
    @SerialName("webpage_url")
    val url: String,
    @SerialName("entries")
    val videos: List<YtDlpPlaylistVideo>
)

sealed class YtDlpVideoInfoResult {
    data class Error(val message: String) : YtDlpVideoInfoResult()
    data class Success(val ytDlpVideo: YtDlpVideo) : YtDlpVideoInfoResult()

    data class AgeRestricted(val ytDlpPlaylistVideo: YtDlpPlaylistVideo) : YtDlpVideoInfoResult()
}


sealed class YtDlpPlaylistResult {
    data class Error(val message: String) : YtDlpPlaylistResult()
    data class Success(val ytDlpPlaylist: YtDlpPlaylist) : YtDlpPlaylistResult()
}