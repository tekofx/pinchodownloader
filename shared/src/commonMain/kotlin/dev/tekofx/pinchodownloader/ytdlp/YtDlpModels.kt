package dev.tekofx.pinchodownloader.ytdlp

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
    val height: Int? = null
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

@Serializable
data class YtVideoOembed(
    val title: String,
    @SerialName("thumbnail_url")
    val thumbnail: String,
)