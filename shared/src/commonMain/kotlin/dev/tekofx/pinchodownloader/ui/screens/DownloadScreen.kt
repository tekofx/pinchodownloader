package dev.tekofx.pinchodownloader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.tekofx.pinchodownloader.entities.TaskStatus
import dev.tekofx.pinchodownloader.entities.Video
import dev.tekofx.pinchodownloader.entities.VideoInfoResult
import dev.tekofx.pinchodownloader.ui.components.AppTitle
import dev.tekofx.pinchodownloader.ui.components.Queue
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
    var showDialog by remember { mutableStateOf(false) }


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

    AnimatedVisibility(visible = showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Spacer(Modifier.height(8.dp))
                    Text("Built on 2025-08-31", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(16.dp))
                    TextButton(onClick = { showDialog = false }) {
                        Text("Close")
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        AppTitle(
            onUpdateTagClick = { showDialog = true }
        )
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
                Icon(
                    imageVector = Icons.Filled.ContentPaste,
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = null
                )
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