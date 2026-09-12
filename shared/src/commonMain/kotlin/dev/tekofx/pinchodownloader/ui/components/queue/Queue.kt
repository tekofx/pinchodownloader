package dev.tekofx.pinchodownloader.ui.components.queue

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.tekofx.pinchodownloader.entities.ButtonVariant
import dev.tekofx.pinchodownloader.entities.Video
import dev.tekofx.pinchodownloader.ui.components.TextIconButton
import dev.tekofx.pinchodownloader.ui.components.video.VideosList

@Composable
fun Queue(
    videos: List<Video>,
    downloading: Boolean,
    progress: Float,
    onDownloadAll: () -> Unit,
    onClearAll: () -> Unit,
    onClearCompleted: () -> Unit,
    onDeleteVideo: (Int) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {


        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                "Queue",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Card {
                Text(modifier = Modifier.padding(5.dp), text = videos.size.toString() + " in queue")
            }

        }


        Row(
            horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
        ) {
            TextIconButton(
                onClick = onDownloadAll, icon = Icons.Filled.Download, text = "Download All",
                enabled = !downloading

            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextIconButton(
                    onClick = onClearAll,
                    icon = Icons.Filled.DeleteSweep,
                    text = "Clear All",
                    variant = ButtonVariant.Outlined,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                )
                TextIconButton(
                    onClick = onClearCompleted,
                    icon = Icons.Filled.DownloadDone,
                    text = "Clear Completed",
                    variant = ButtonVariant.Outlined,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    ),
                )
            }
        }

        AnimatedVisibility(downloading) {
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
        }
        VideosList(
            videos = videos,
            onDeleteVideo = onDeleteVideo
        )
    }

}