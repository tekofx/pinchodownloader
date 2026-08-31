package dev.tekofx.pinchodownloader.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.tekofx.pinchodownloader.entities.Video


@Composable
fun VideosList(videos: List<Video>) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        videos.forEach { video ->
            VideoCard(video)
        }
    }
}


@Composable
fun VideoCard(video: Video) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row {
            Text(text = video.id.toString())
            AsyncImage(
                model = video.thumbnail,
                contentDescription = null,
                modifier = Modifier.size(96.dp, 54.dp),
                contentScale = ContentScale.Crop
            )
            Text(text = video.title)
        }
    }
}