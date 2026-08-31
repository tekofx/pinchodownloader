package dev.tekofx.pinchodownloader

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        DownloaderScreen()
    }
}