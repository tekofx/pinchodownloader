package dev.tekofx.pinchodownloader.ui.components.video

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.tekofx.pinchodownloader.entities.TaskStatus
import dev.tekofx.pinchodownloader.entities.Video


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun VideoCard(
    video: Video,
    modifier: Modifier,
    onDelete: () -> Unit,
) {

    val animatedProgress by animateFloatAsState(
        targetValue = video.progress,
        animationSpec = tween(300),
        label = "progress"
    )

    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(10.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(columnSpacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            Text(
                text = video.id.toString(), modifier = Modifier.weight(layout.id)
            )
            AsyncImage(
                model = video.thumbnail,
                contentDescription = null,
                modifier = Modifier.weight(layout.thumbnail).height(54.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = video.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(layout.title)
            )

            Row(modifier = Modifier.weight(layout.format)) {
                Card(
                    colors = CardDefaults.cardColors().copy(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Icon(imageVector = Icons.Default.VideoSettings, contentDescription = null)
                        Text(text = video.format)
                    }
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