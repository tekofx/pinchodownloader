package dev.tekofx.pinchodownloader.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.tekofx.pinchodownloader.entities.ButtonVariant
import dev.tekofx.pinchodownloader.entities.Video

@Composable
fun Queue(
    videos: List<Video>,
    downloading: Boolean,
    progress: Float,
    onDownloadAll: () -> Unit,
    onClear: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Queue",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )


        if (videos.isEmpty()) {
            Text(
                "Queue Empty",
                color = MaterialTheme.colorScheme.onSurface,
            )
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
                    text = "Clear Queue",
                    variant = ButtonVariant.Outlined
                )
            }
        }

        AnimatedVisibility(downloading) {
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
        }
        VideosList(videos)
    }

}