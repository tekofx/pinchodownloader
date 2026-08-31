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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
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
import dev.tekofx.pinchodownloader.ui.components.VideosList
import kotlinx.coroutines.launch

@Composable
fun DownloaderScreen() {
    val scope = rememberCoroutineScope()
    var url by remember { mutableStateOf("") }
    var progress by remember { mutableFloatStateOf(0f) }
    var status by remember { mutableStateOf("") }
    val videos = remember { mutableStateListOf<Video>() }

    suspend fun addToQueue() {
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
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(8.dp),
    ) {

        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("Video URL") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
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


        Button(onClick = {
            scope.launch {
                for (i in videos.indices) {
                    videos[i] = videos[i].copy(status = TaskStatus.IN_PROGRESS)  // ← new instance
                    downloadYtDlp(videos[i].url, getDownloadsDir()) { p ->
                        videos[i] = videos[i].copy(progress = p.toFloat())
                    }
                    videos[i] = videos[i].copy(status = TaskStatus.COMPLETED)   // ← new instance
                    progress = i.toFloat() * 100 / videos.size

                }
                status = "Done"
            }
        }, enabled = videos.isNotEmpty()) { Text("Download All") }

        AnimatedVisibility(progress != 0f) {
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
        }
        Text(status)
        VideosList(videos)


    }
}