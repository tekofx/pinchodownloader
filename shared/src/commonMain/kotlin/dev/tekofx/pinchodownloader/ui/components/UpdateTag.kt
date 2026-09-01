package dev.tekofx.pinchodownloader.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UpdateTag(
) {
    var appUpdateState by remember { mutableStateOf(UpdateState.Checking) }
    var ytDlpUpdateState by remember { mutableStateOf(UpdateState.Checking) }
    var showDialog by remember { mutableStateOf(false) }
    val globalUpdateState by remember {
        derivedStateOf {
            when {
                ytDlpUpdateState == UpdateState.Checking || appUpdateState == UpdateState.Checking -> UpdateState.Checking
                ytDlpUpdateState == UpdateState.UpdateAvailable || appUpdateState == UpdateState.UpdateAvailable -> UpdateState.UpdateAvailable
                else -> UpdateState.UpToDate
            }
        }
    }

    LaunchedEffect(Unit) {
        ytDlpUpdateState = when {
            checkYtDlpUpdate() -> UpdateState.UpdateAvailable
            else -> UpdateState.UpToDate
        }
        appUpdateState = when {
            checkPinchoDownloaderUpdate() -> UpdateState.UpdateAvailable
            else -> UpdateState.UpToDate
        }

    }

    suspend fun updateYtDlp() {
        ytDlpUpdateState = UpdateState.Updating
        try {
            val exitCode = withContext(Dispatchers.IO) {
                ProcessBuilder("yt-dlp", "-U").redirectErrorStream(true).start().waitFor()
            }

            if (exitCode == 0) {
                // Silent update succeeded → re-check after a short delay
                delay(1000.milliseconds)
            } else {
                // Failed → open PowerShell
                withContext(Dispatchers.IO) {
                    ProcessBuilder(
                        "powershell.exe", "-NoExit", "-Command", "yt-dlp -U"
                    ).start()
                }
                // Poll every 10s for up to 2 min waiting for the update to land
                repeat(12) {
                    Thread.sleep(10_000)
                    if (!checkYtDlpUpdate()) return@repeat
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.IO) {
                ProcessBuilder(
                    "powershell.exe", "-NoExit", "-Command", "yt-dlp -U"
                ).start()
            }
            repeat(12) {
                Thread.sleep(10_000)
                if (!checkYtDlpUpdate()) return@repeat
            }
        }

        // Final re-check → update UI state
        ytDlpUpdateState = when {
            checkYtDlpUpdate() -> UpdateState.UpdateAvailable
            else -> UpdateState.UpToDate
        }

        ytDlpUpdateState = UpdateState.UpToDate
    }


    AnimatedVisibility(visible = globalUpdateState == UpdateState.Checking) {
        LoadingIndicator(
            modifier = Modifier.size(30.dp)
        )
    }
    AnimatedVisibility(visible = globalUpdateState == UpdateState.UpdateAvailable) {
        IconButton(
            onClick = { showDialog = true },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,   // background
                contentColor = MaterialTheme.colorScheme.onPrimary            // icon tint
            )
        ) {
            Icon(
                modifier = Modifier.size(30.dp), imageVector = Icons.Filled.Download, contentDescription = null
            )
        }
    }

    UpdateDialog(
        showDialog = showDialog,
        appUpdateState = appUpdateState,
        ytDlpUpdateState = ytDlpUpdateState,
        onDismissRequest = { showDialog = false },
        onUpdateYtdlpClick = { updateYtDlp() })
}

@Composable
fun UpdateDialog(
    showDialog: Boolean,
    appUpdateState: UpdateState,
    ytDlpUpdateState: UpdateState,
    onDismissRequest: () -> Unit,
    onUpdateYtdlpClick: suspend () -> Unit
) {
    AnimatedVisibility(visible = showDialog) {
        Dialog(onDismissRequest = onDismissRequest) {
            Surface(
                shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AppPart(
                            appUpdateState = appUpdateState
                        )
                        YtDlpPart(
                            ytDlpUpdateState = ytDlpUpdateState, onClick = onUpdateYtdlpClick
                        )


                    }
                    TextIconButton(
                        onClick = { onDismissRequest() }, icon = Icons.Filled.Close, text = "Close"
                    )

                }
            }
        }
    }
}


@Composable
fun AppPart(
    appUpdateState: UpdateState,
) {
    val uriHandler = LocalUriHandler.current
    Column {
        Text("App Update", style = MaterialTheme.typography.headlineSmall)
        Button(
            enabled = appUpdateState == UpdateState.UpdateAvailable,
            onClick = { uriHandler.openUri("https://github.com/tekofx/pinchodownloader/releases/latest") }) {
            when (appUpdateState) {
                UpdateState.UpToDate -> {
                    Text("Up to date")
                }

                UpdateState.UpdateAvailable -> {
                    Text("Update")
                }

                else -> null
            }
        }

    }
}

@Composable
fun YtDlpPart(
    ytDlpUpdateState: UpdateState,
    onClick: suspend () -> Unit,
) {
    val scope = rememberCoroutineScope()

    Column {
        Text("YtDlp Updates", style = MaterialTheme.typography.headlineSmall)
        Button(
            enabled = ytDlpUpdateState == UpdateState.UpdateAvailable, onClick = {
                scope.launch {
                    onClick()
                }
            }) {
            when (ytDlpUpdateState) {
                UpdateState.UpdateAvailable -> {
                    Text("Update Yt-dlp")
                }

                UpdateState.Updating -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LoadingIndicator(color = MaterialTheme.colorScheme.secondary)
                        Text("Updating")
                    }
                }

                UpdateState.UpToDate -> {
                    Text("Up to date")
                }

                else -> null
            }
        }

    }
}