package dev.tekofx.pinchodownloader

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.tekofx.pinchodownloader.ui.theme.AppTheme

@Composable
@Preview
fun App() {
    AppTheme {
        Surface {
            DownloaderScreen()
        }
    }
}