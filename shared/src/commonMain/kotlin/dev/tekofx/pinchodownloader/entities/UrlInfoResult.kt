package dev.tekofx.pinchodownloader.entities

sealed class UrlInfoResult {

    data class SingleVideo(val videoInfo: VideoInfo) : UrlInfoResult()

    data class Playlist(val videosInfo: List<VideoInfo>) : UrlInfoResult()

    data class Error(val message: String) : UrlInfoResult()
}


data class VideoInfo(val title: String, val thumbnail: String, val url: String, val format: String)


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