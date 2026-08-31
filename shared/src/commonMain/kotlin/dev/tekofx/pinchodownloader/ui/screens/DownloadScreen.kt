package dev.tekofx.pinchodownloader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import dev.tekofx.pinchodownloader.entities.TaskStatus
import dev.tekofx.pinchodownloader.entities.Video
import dev.tekofx.pinchodownloader.entities.VideoInfoResult
import dev.tekofx.pinchodownloader.ui.components.Queue
import dev.tekofx.pinchodownloader.ui.components.TextIconButton
import dev.tekofx.pinchodownloader.ui.components.VideosList
import kotlinx.coroutines.launch
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor

@Composable
fun DownloaderScreen() {
    val scope = rememberCoroutineScope()
    var url by remember { mutableStateOf("") }
    var progress by remember { mutableFloatStateOf(0f) }
    var status by remember { mutableStateOf("") }
    val videos = remember { mutableStateListOf<Video>() }
    var loading by remember { mutableStateOf(false) }
    var downloading by remember { mutableStateOf(false) }

    fun pasteFromClipboard(): String? {
        return try {
            Toolkit.getDefaultToolkit().systemClipboard
                .getData(DataFlavor.stringFlavor) as? String
        } catch (_: Exception) {
            null
        }
    }

    suspend fun addToQueue() {
        loading = true
        when (val result = getVideoInfo(url)) {
            is VideoInfoResult.Success -> {
                videos.add(
                    Video(
                        id = videos.size + 1,
                        title = result.title,
                        thumbnail = result.thumbnail,
                        url = url
                    )
                )
                url = ""
            }

            is VideoInfoResult.Error -> {
                status = result.message
            }
        }
        loading = false
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("Video URL") },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .onPreviewKeyEvent { event ->
                        if (event.type == KeyEventType.KeyDown && event.key == Key.Enter) {
                            if (url.isNotBlank()) {
                                scope.launch {
                                    addToQueue()
                                    url = ""  // clear the field
                                }
                            }
                            true // consume the event
                        } else {
                            false
                        }
                    }
            )
            IconButton(
                onClick = {
                    val clipboardContent = pasteFromClipboard()
                    if (clipboardContent != null) {
                        url = clipboardContent
                    }
                    scope.launch {
                        addToQueue()
                        url = ""
                    }
                },
            ) {
                Icon(imageVector = Icons.Filled.ContentPaste, null)
            }
            IconButton(
                onClick = {
                    url = ""
                },
            ) {
                Icon(imageVector = Icons.Filled.Delete, null)
            }
        }

        AnimatedVisibility(visible = loading) {
            Card {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Adding video")
                    LoadingIndicator()
                }
            }
        }

        Queue(
            videos = videos,
            downloading = downloading,
            progress = progress,
            onDownloadAll = {
                scope.launch {
                    downloading = true
                    for (i in videos.indices) {
                        videos[i] = videos[i].copy(status = TaskStatus.IN_PROGRESS)  // ← new instance
                        downloadYtDlp(videos[i].url, getDownloadsDir()) { p ->
                            videos[i] = videos[i].copy(progress = p.toFloat())
                            progress = (i + p).toFloat() / videos.size
                        }
                        videos[i] = videos[i].copy(status = TaskStatus.COMPLETED)   // ← new instance
                    }
                    progress = 1f
                    status = "Done"
                    downloading = false
                }
            },
            onClear = { videos.clear() }

        )


    }
}