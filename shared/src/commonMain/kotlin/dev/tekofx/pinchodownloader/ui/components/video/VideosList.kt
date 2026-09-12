package dev.tekofx.pinchodownloader.ui.components.video

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.tekofx.pinchodownloader.entities.Video

data class ColumnLayout(
    val id: Float = 0.02f,
    val thumbnail: Float = 0.1f,
    val title: Float = 0.25f,
    val format: Float = 0.15f,
    val status: Float = 0.1f,
)

val layout = ColumnLayout()
val columnSpacing = 15.dp

@Composable
fun VideosList(videos: List<Video>) {


    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        stickyHeader {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    contentColor = MaterialTheme.colorScheme.onSurface,

                    ),
                shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 0)),
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(columnSpacing)
                ) {
                    Text(
                        text = "Id",
                        modifier = Modifier.weight(layout.id)
                    )
                    Text(
                        text = "Thumbnail",
                        modifier = Modifier.weight(layout.thumbnail)
                    )
                    Text(
                        text = "Title",
                        modifier = Modifier.weight(layout.title)
                    )
                    Text(
                        text = "Format",
                        modifier = Modifier.weight(layout.format)
                    )
                    Text(
                        text = "Status",
                        modifier = Modifier.weight(layout.status)
                    )
                }
            }
        }
        items(videos, key = { it.id }) { video ->
            VideoCard(
                video = video,
                modifier = Modifier.animateItem()
            )
        }
    }
}


