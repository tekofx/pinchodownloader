package dev.tekofx.pinchodownloader.entities

sealed class UrlInfoResult {

    data class SingleVideo(val videoInfo: VideoInfo) : UrlInfoResult()

    data class Playlist(val videosInfo: List<VideoInfo>) : UrlInfoResult()

    data class Error(val message: String) : UrlInfoResult()
}


data class VideoInfo(val title: String, val thumbnail: String, val url: String, val format: String)