package dev.tekofx.pinchodownloader.ui.components.video

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import dev.tekofx.pinchodownloader.entities.TaskStatus
import dev.tekofx.pinchodownloader.entities.Video
import dev.tekofx.pinchodownloader.entities.VideoFormat
import dev.tekofx.pinchodownloader.log.LogStore


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun VideoCard(
    index: Int,
    video: Video,
    modifier: Modifier,
    onDelete: () -> Unit,
    onFormatSelection: (VideoFormat?) -> Unit,
) {

    val animatedProgress by animateFloatAsState(
        targetValue = video.progress,
        animationSpec = tween(300),
        label = "progress"
    )

    val platformContext = LocalPlatformContext.current

    // Make coil try to fetch thumbnails until it gets one
    val imageRequest = ImageRequest.Builder(platformContext)
        .data(video.thumbnails)
        .crossfade(true)
        .build()

    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(10.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(columnSpacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            Text(
                text = "${index + 1}", modifier = Modifier.weight(layout.id)
            )
            VideoThumbnail(
                video = video,
                modifier = Modifier.weight(layout.thumbnail).height(54.dp)
            )
            Text(
                text = video.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(layout.title)
            )

            Row(modifier = Modifier.weight(layout.format)) {
                if (video.status == TaskStatus.AGE_RESTRICTED) {
                    Text("Age Restricted")
                } else {
                    FormatComboBox(
                        formats = video.formats,
                        selectedFormat = video.selectedFormat,
                        onFormatSelected = onFormatSelection
                    )
                }
            }

            Row(
                modifier = Modifier.weight(layout.status),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                AnimatedContent(
                    targetState = video.status, transitionSpec = {
                        fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                    }, modifier = Modifier.size(24.dp) // fixed size so layout doesn't jump
                ) { status ->
                    when (status) {
                        TaskStatus.PENDING -> Icon(
                            imageVector = Icons.Filled.HourglassBottom, contentDescription = null, tint = status.color
                        )

                        TaskStatus.IN_PROGRESS -> CircularWavyProgressIndicator(
                            progress = { animatedProgress }, color = status.color, modifier = Modifier.fillMaxSize()
                        )

                        TaskStatus.COMPLETED -> Icon(
                            imageVector = Icons.Filled.Check, contentDescription = null, tint = status.color
                        )

                        TaskStatus.ERROR -> Icon(
                            imageVector = Icons.Filled.Close, contentDescription = null, tint = status.color
                        )

                        TaskStatus.AGE_RESTRICTED -> Icon(
                            imageVector = Icons.Filled.Close, contentDescription = null, tint = status.color
                        )
                    }
                }


                Text(text = video.status.label, color = video.status.color)


            }
            Row(
                modifier = Modifier.weight(layout.status),
            ) {
                IconButton(
                    onClick = onDelete
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun VideoThumbnail(
    video: Video,
    modifier: Modifier = Modifier
) {
    val platformContext = LocalPlatformContext.current

    var currentThumbnailIndex by remember(video.id) { mutableStateOf(0) }

    val currentUrl = video.thumbnails.getOrNull(currentThumbnailIndex)

    if (!currentUrl.isNullOrBlank()) {
        val imageRequest = ImageRequest.Builder(platformContext)
            .data(currentUrl)
            .crossfade(true)
            .build()

        AsyncImage(
            model = imageRequest,
            contentDescription = "Video Thumbnail",
            contentScale = ContentScale.Crop,
            modifier = modifier.size(width = 120.dp, height = 90.dp),
            onError = { errorState ->
                LogStore.warn(
                    "VideoThumbnail",
                    "Fallo al cargar miniatura en índice $currentThumbnailIndex: ${errorState.result.throwable.message}"
                )

                if (currentThumbnailIndex < video.thumbnails.lastIndex) {
                    currentThumbnailIndex++
                }
            }
        )
    } else {
        AsyncImage(
            model = "https://i.ytimg.com/vi_webp/b58P4phxXxA/maxresdefault.webp",
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
    }
}