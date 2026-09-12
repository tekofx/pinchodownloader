package dev.tekofx.pinchodownloader.ui.components.queue

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.tekofx.pinchodownloader.ui.components.LoadingCat

@Composable
fun EmptyQueue() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Empty Queue", style = MaterialTheme.typography.headlineLarge)
        Text(
            text = "Try adding some videos",
            style = MaterialTheme.typography.bodyLarge
        )

        LoadingCat()

    }
}