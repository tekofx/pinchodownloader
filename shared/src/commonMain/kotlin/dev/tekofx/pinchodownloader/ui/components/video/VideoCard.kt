package dev.tekofx.pinchodownloader.ui.components.video

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.VideoSettings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.tekofx.pinchodownloader.entities.TaskStatus
import dev.tekofx.pinchodownloader.entities.Video

@Composable
fun VideoCard(video: Video, modifier: Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(10.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Text(text = video.id.toString())
                AsyncImage(
                    model = video.thumbnail,
                    contentDescription = null,
                    modifier = Modifier.size(96.dp, 54.dp),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = video.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )


            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.wrapContentWidth()
            ) {
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


                AnimatedContent(
                    targetState = video.status,
                    transitionSpec = {
                        fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                    },
                    modifier = Modifier.size(24.dp) // fixed size so layout doesn't jump
                ) { status ->
                    when (status) {
                        TaskStatus.PENDING -> Icon(
                            imageVector = Icons.Filled.HourglassBottom,
                            contentDescription = null,
                            tint = status.color
                        )

                        TaskStatus.IN_PROGRESS -> CircularProgressIndicator(
                            progress = { video.progress },
                            color = status.color,
                            modifier = Modifier.fillMaxSize()
                        )

                        TaskStatus.COMPLETED -> Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = status.color
                        )

                        TaskStatus.ERROR -> Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = null,
                            tint = status.color
                        )
                    }
                }


                Text(text = video.status.label, color = video.status.color)
            }
        }
    }
}