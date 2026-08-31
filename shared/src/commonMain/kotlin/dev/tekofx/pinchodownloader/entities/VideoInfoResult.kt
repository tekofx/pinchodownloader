package dev.tekofx.pinchodownloader.entities

sealed class VideoInfoResult {
    data class Success(val title: String, val thumbnail: String) : VideoInfoResult()
    data class Error(val message: String) : VideoInfoResult()
}