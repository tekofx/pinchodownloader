package dev.tekofx.pinchodownloader.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.tekofx.pinchodownloader.entities.TaskStatus
import dev.tekofx.pinchodownloader.entities.Video
import dev.tekofx.pinchodownloader.entities.VideoInfoResult
import dev.tekofx.pinchodownloader.entities.states.DownloaderState
import dev.tekofx.pinchodownloader.filesystem.getDownloadsDir
import dev.tekofx.pinchodownloader.log.LogStatus
import dev.tekofx.pinchodownloader.log.LogStore
import dev.tekofx.pinchodownloader.ytdlp.downloadYtDlp
import dev.tekofx.pinchodownloader.ytdlp.getVideoInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DownloaderViewModel : ViewModel() {
    private val _state = MutableStateFlow(DownloaderState())
    val state: StateFlow<DownloaderState> = _state

    private val _snackbar = MutableStateFlow<String?>(null)
    val snackbar: StateFlow<String?> = _snackbar

    fun addToQueue() {
        _state.update { it.copy(loading = true) }
        viewModelScope.launch {
            when (val result = getVideoInfo(_state.value.url)) {
                is VideoInfoResult.Success -> {
                    if (_state.value.videos.any { it.url == result.url }) {
                        _snackbar.value = "Video already in queue"
                    } else {
                        _state.update { s ->
                            s.copy(
                                videos = s.videos + Video(
                                    id = s.videos.size + 1,
                                    title = result.title,
                                    thumbnail = result.thumbnail,
                                    url = state.value.url,
                                    format = result.format
                                )
                            )
                        }
                    }
                    onUrlChange("")
                }

                is VideoInfoResult.Error -> {
                    _state.update { it.copy(status = result.message) }
                }
            }
            _state.update { it.copy(loading = false) }
        }
    }

    fun downloadAll() {
        viewModelScope.launch {
            _state.update { it.copy(downloading = true) }
            val videos = _state.value.videos

            for (i in videos.indices) {
                updateVideo(i) { it.copy(status = TaskStatus.IN_PROGRESS) }
                try {
                    downloadYtDlp(videos[i].url, getDownloadsDir()) { p ->
                        updateVideo(i) { it.copy(progress = p.toFloat()) }
                        _state.update { s ->
                            s.copy(progress = ((i + p) / s.videos.size).toFloat())
                        }
                    }
                } catch (e: RuntimeException) {
                    LogStore.log("YT-Dlp Download", e.message ?: "Unknown", LogStatus.ERROR)
                    updateVideo(i) { it.copy(status = TaskStatus.ERROR) }
                    continue
                }
                updateVideo(i) { it.copy(status = TaskStatus.COMPLETED) }
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

    private fun updateVideo(index: Int, transform: (Video) -> Video) {
        _state.update { s ->
            s.copy(videos = s.videos.toMutableList().also { it[index] = transform(it[index]) })
        }
    }

    fun dismissSnackbar() {
        _snackbar.value = null
    }
}