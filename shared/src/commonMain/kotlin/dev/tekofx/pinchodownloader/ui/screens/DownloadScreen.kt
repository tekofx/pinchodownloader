package dev.tekofx.pinchodownloader.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.tekofx.pinchodownloader.pasteFromClipboard
import dev.tekofx.pinchodownloader.ui.components.*
import dev.tekofx.pinchodownloader.viewmodels.DownloaderViewModel

@Composable
fun DownloaderScreen(
    viewModel: DownloaderViewModel = viewModel { DownloaderViewModel() }
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var url by remember { mutableStateOf("") }
    var showLogs by remember { mutableStateOf(false) }

    val snackbarMessage by viewModel.snackbar.collectAsState()

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissSnackbar()
        }
    }

    Scaffold(
        topBar = { AppTitle(onIconClick = { showLogs = true }) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(visible = showLogs) {
                LogView(onCloseClick = { showLogs = false })
            }

            UrlInputRow(
                url = url,
                onUrlChange = { url = it },
                onSubmit = { viewModel.addToQueue(url); url = "" },
                onPaste = {
                    pasteFromClipboard()?.let { url = it }
                    viewModel.addToQueue(url)
                    url = ""
                }
            )

            AnimatedVisibility(visible = state.loading) {
                LoadingCard()
            }

            Queue(
                videos = state.videos,
                downloading = state.downloading,
                progress = state.progress,
                onDownloadAll = viewModel::downloadAll,
                onClearAll = viewModel::clearAll,
                onClearCompleted = viewModel::clearCompleted
            )
        }
    }
}