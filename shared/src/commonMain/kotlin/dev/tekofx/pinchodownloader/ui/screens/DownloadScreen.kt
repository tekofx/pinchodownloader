package dev.tekofx.pinchodownloader

import androidx.compose.animation.AnimatedVisibility
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
import dev.tekofx.pinchodownloader.entities.Video
import dev.tekofx.pinchodownloader.ui.components.VideosList
import kotlinx.coroutines.launch

@Composable
fun DownloaderScreen() {
    val scope = rememberCoroutineScope()
    var url by remember { mutableStateOf(TextFieldValue("")) }

    var progress by remember { mutableFloatStateOf(0f) }
    var status by remember { mutableStateOf("") }
    var videos by remember { mutableStateOf<List<Video>>(emptyList()) }

    suspend fun addToQueue(newUrl: String) {
        val (title, thumbnail) = getVideoInfo(newUrl)
        videos = videos + Video(id = videos.size + 1, title = title, thumbnail = thumbnail)
    }

    Column(
    ) {

        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("Video URL") },
            modifier = Modifier.fillMaxWidth()
        )

        /*OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("Video URL") },
            modifier = Modifier.fillMaxWidth()
        )
*/


        //VideosList(videos)

        /*AnimatedVisibility(visible = url.isNotBlank()) {
            Button(onClick = {
                scope.launch {
                    status = "Downloading..."
                    downloadYtDlp(url, getDownloadsDir()) { p ->
                        progress = p.toFloat()  // already on Main via rememberCoroutineScope
                    }
                    status = "Done"
                }
            }, enabled = url.isNotBlank()) { Text("Download") }
        }

        AnimatedVisibility(progress != 0f) {
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
            Text(status)
        }*/

    }
}