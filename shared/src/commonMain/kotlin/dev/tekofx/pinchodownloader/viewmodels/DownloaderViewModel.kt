package dev.tekofx.pinchodownloader.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.tekofx.pinchodownloader.entities.PlaylistInfoResult
import dev.tekofx.pinchodownloader.entities.TaskStatus
import dev.tekofx.pinchodownloader.entities.Video
import dev.tekofx.pinchodownloader.entities.VideoInfoResult
import dev.tekofx.pinchodownloader.entities.states.DownloaderState
import dev.tekofx.pinchodownloader.filesystem.getDownloadsDir
import dev.tekofx.pinchodownloader.log.LogStore
import dev.tekofx.pinchodownloader.ytdlp.downloadYtDlp
import dev.tekofx.pinchodownloader.ytdlp.getOEmbed
import dev.tekofx.pinchodownloader.ytdlp.getPlaylistInfo
import dev.tekofx.pinchodownloader.ytdlp.getVideoInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URI

class DownloaderViewModel : ViewModel() {
    private val _state = MutableStateFlow(DownloaderState())
    val state: StateFlow<DownloaderState> = _state

    private val _snackbar = MutableStateFlow<String?>(null)
    val snackbar: StateFlow<String?> = _snackbar

    fun onSubmitUrl() {

        if (!isValidUrl(_state.value.url)) {
            _snackbar.value = "Url not valid"
            return
        }


        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            if (_state.value.url.contains("&list=")) {
                addPlaylistToQueue(_state.value.url)

            } else {
                addSingleVideoToQueue(_state.value.url)
            }
            _state.update { it.copy(loading = false) }
        }

    }


    private suspend fun addSingleVideoToQueue(url: String) {
        if (_state.value.videos.any { it.url == url }) {
            LogStore.debug("addToQueue", "Video already in queue")
            _snackbar.value = "Video already in queue"
        } else {
            when (val videoInfoResult = getVideoInfo(url)) {
                is VideoInfoResult.Success -> {
                    val videoInfo = videoInfoResult.videoInfo
                    _state.update { s ->
                        s.copy(
                            videos = s.videos + Video(
                                id = s.videos.size + 1,
                                title = videoInfo.title,
                                thumbnail = videoInfo.thumbnail,
                                url = url,
                                format = videoInfo.format
                            )
                        )
                    }
                }


                is VideoInfoResult.AgeRestricted -> {
                    val minimalVideoInfo = videoInfoResult.minimalVideoInfo
                    _state.update { s ->
                        s.copy(
                            videos = s.videos + Video(
                                id = s.videos.size + 1,
                                title = minimalVideoInfo.title,
                                thumbnail = minimalVideoInfo.thumbnail,
                                url = state.value.url,
                                format = "Age Restricted",
                                status = TaskStatus.ERROR
                            )
                        )
                    }
                }

                is VideoInfoResult.Error -> {
                    LogStore.error("addSingleVideoToQueue", videoInfoResult.toString())
                    val test = getOEmbed(url)
                    LogStore.error("addSingleVideoToQueue", test.toString())

                    _state.update { it.copy(status = videoInfoResult.message) }

                }
            }

        }
    }

    private suspend fun addPlaylistToQueue(url: String) {

        when (val playlistInfoResult = getPlaylistInfo(url)) {
            is PlaylistInfoResult.Success -> {
                _state.update { it.copy(loading = true, loaderMessage = "Adding playlist videos") }

                val minimalVideoInfoList = playlistInfoResult.playlistInfo

                minimalVideoInfoList.videos.forEachIndexed { index, minimalVideoInfo ->
                    addSingleVideoToQueue(minimalVideoInfo.url)
                    _state.update {
                        it.copy(
                            loading = true,
                            loaderMessage = "Added ${index + 1}/${minimalVideoInfoList.videos.size}"
                        )
                    }
                }
            }

            is PlaylistInfoResult.Error -> {
                LogStore.error("addPlaylistToQueue", playlistInfoResult.toString())
                _state.update { it.copy(status = playlistInfoResult.message) }
            }
        }

    }


    fun isValidUrl(url: String): Boolean = try {
        val uri = URI(url)
        uri.scheme != null && uri.host != null
    } catch (e: Exception) {
        LogStore.error("isValidUrl", e.printStackTrace().toString())
        false
    }

    fun downloadAll() {
        viewModelScope.launch {
            _state.update { it.copy(progress = 0f, downloading = true, latestDownloadVideoIndex = 0) }

            val snapshotVideos = _state.value.videos

            snapshotVideos.forEachIndexed { index, video ->
                if (video.status == TaskStatus.ERROR) return@forEachIndexed


                val videoId = video.id

                updateVideo(videoId) { it.copy(status = TaskStatus.IN_PROGRESS) }

                try {
                    downloadYtDlp(video.url, getDownloadsDir()) { p ->
                        updateVideo(videoId) { it.copy(progress = p.toFloat()) }
                        _state.update { s ->
                            s.copy(progress = ((index + p) / snapshotVideos.size).toFloat())
                        }
                    }
                } catch (e: RuntimeException) {
                    LogStore.error("YT-Dlp Download", e.message ?: "Unknown")
                    updateVideo(videoId) { it.copy(status = TaskStatus.ERROR) }
                    return@forEachIndexed
                }

                updateVideo(videoId) { it.copy(status = TaskStatus.COMPLETED) }
                _state.update { s -> s.copy(latestDownloadVideoIndex = index + 1) }
            }

            _state.update { it.copy(progress = 1f, status = "Done", downloading = false) }
        }
    }


    fun onUrlChange(url: String) {
        _state.update { it.copy(url = url) }
    }

    fun toggleLogs() {
        _state.update { it.copy(showLogs = !it.showLogs) }
    }

    fun clearCompleted() {
        _state.update { s -> s.copy(videos = s.videos.filter { it.status != TaskStatus.COMPLETED }) }
    }

    fun clearAll() {
        _state.update { it.copy(videos = emptyList()) }
    }

    fun deleteVideo(id: Int) {
        _state.update { s -> s.copy(videos = s.videos.filter { it.id != id }) }
    }

    fun updateVideo(videoId: Int, transform: (Video) -> Video) {
        _state.update { currentState ->
            currentState.copy(
                videos = currentState.videos.map { video ->
                    if (video.id == videoId) transform(video) else video
                }
            )
        }
    }


    fun dismissSnackbar() {
        _snackbar.value = null
    }
}