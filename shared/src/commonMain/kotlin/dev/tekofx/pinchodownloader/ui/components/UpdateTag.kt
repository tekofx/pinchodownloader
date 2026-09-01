package dev.tekofx.pinchodownloader.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.onClick
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.tekofx.pinchodownloader.checkPinchoDownloaderUpdate
import dev.tekofx.pinchodownloader.checkYtDlpUpdate
import dev.tekofx.pinchodownloader.entities.UpdateState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UpdateTag(
) {
    var newAppVersion by remember { mutableStateOf(UpdateState.Checking) }
    var newYtdlpVersion by remember { mutableStateOf(UpdateState.Checking) }
    var showDialog by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current
    LaunchedEffect(Unit) {
        newYtdlpVersion = when {
            checkYtDlpUpdate() -> UpdateState.UpdateAvailable
            else -> UpdateState.UpToDate
        }
        newAppVersion = when {
            checkPinchoDownloaderUpdate() -> UpdateState.UpdateAvailable
            else -> UpdateState.UpToDate
        }
    }

    if (newYtdlpVersion == UpdateState.UpdateAvailable || newAppVersion == UpdateState.UpdateAvailable) {
        Card(
            modifier = Modifier.onClick(onClick = { showDialog = true }),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.elevatedCardElevation()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(Icons.Filled.Download, null)
                Text(
                    modifier = Modifier.padding(10.dp),
                    text = "Update available",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
    AnimatedVisibility(visible = showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (newAppVersion == UpdateState.UpdateAvailable) {
                        Text("App Update")
                        Button(
                            onClick = { uriHandler.openUri("https://github.com/tekofx/pinchodownloader/releases/latest") }
                        ) {
                            Text("Download new app version")
                        }
                    }

                    if (newYtdlpVersion == UpdateState.UpdateAvailable) {
                        Text("New ytdlp version")
                        Button(
                            onClick = {
                                ProcessBuilder(
                                    "cmd", "/c", "start",
                                    "powershell.exe", "-NoExit", "-Command", "Write-Host 'Hello from PowerShell'"
                                ).start()
                            }
                        ) {
                            Text("Update Yt-dlp")
                        }
                    }

                    Button(onClick = { showDialog = false }) {
                        Text("Close")
                    }

                }
            }
        }
    }
}
