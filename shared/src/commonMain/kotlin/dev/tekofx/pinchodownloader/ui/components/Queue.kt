package dev.tekofx.pinchodownloader.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.tekofx.pinchodownloader.downloadYtDlp
import dev.tekofx.pinchodownloader.entities.TaskStatus
import dev.tekofx.pinchodownloader.entities.Video
import dev.tekofx.pinchodownloader.getDownloadsDir
import kotlinx.coroutines.launch

@Composable
fun Queue(
    videos: List<Video>,
    onDownloadAll: () -> Unit,
    onClear: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Queue", style = MaterialTheme.typography.headlineSmall)

        if (videos.isEmpty()) {
            Text("Queue Empty")
        }

        AnimatedVisibility(videos.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextIconButton(
                    onClick = onDownloadAll,
                    icon = Icons.Filled.Download,
                    text = "Download All"
                )
                TextIconButton(
                    onClick = onClear,
                    icon = Icons.Filled.ClearAll,
                    text = "Clear Queue"
                )
            }
        }

        VideosList(videos)
    }

}