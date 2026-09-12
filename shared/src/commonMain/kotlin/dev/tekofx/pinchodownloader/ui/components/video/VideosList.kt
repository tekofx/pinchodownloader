package dev.tekofx.pinchodownloader.ui.components.video

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.tekofx.pinchodownloader.entities.Video


@Composable
fun VideosList(videos: List<Video>) {
    LazyColumn(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(videos, key = { it.id }) { video ->
            VideoCard(
                video = video,
                modifier = Modifier.animateItem(

                )
            )
        }
    }
}


