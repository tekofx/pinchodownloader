package dev.tekofx.pinchodownloader.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.tekofx.pinchodownloader.ui.components.AppTitle
import dev.tekofx.pinchodownloader.ui.components.LoadingCard
import dev.tekofx.pinchodownloader.ui.components.LogView
import dev.tekofx.pinchodownloader.ui.components.input.UrlTextField
import dev.tekofx.pinchodownloader.ui.components.queue.EmptyQueue
import dev.tekofx.pinchodownloader.ui.components.queue.Queue
import dev.tekofx.pinchodownloader.viewmodels.DownloaderViewModel

@Composable
fun DownloaderScreen(
    viewModel: DownloaderViewModel = viewModel { DownloaderViewModel() }
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val snackbarMessage by viewModel.snackbar.collectAsState()


    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissSnackbar()
        }
    }

    Scaffold(
        topBar = { AppTitle(onIconClick = viewModel::toggleLogs) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(visible = state.showLogs) {
                LogView(onCloseClick = viewModel::toggleLogs)
            }

            UrlTextField(
                url = state.url,
                onUrlChange = viewModel::onUrlChange,
                onSubmit = viewModel::addToQueue,
            )

            AnimatedVisibility(visible = state.loading) {
                LoadingCard()
            }


            Spacer(modifier = Modifier.height(30.dp))

            AnimatedContent(
                targetState = state.videos.isNotEmpty(), transitionSpec = {
                    slideInVertically(tween(300)) { it / 4 } + fadeIn(tween(300)) togetherWith slideOutVertically(
                        tween(300)
                    ) { -it / 4 } + fadeOut(tween(300))
                }) { showSecondState ->
                if (showSecondState) Queue(
                    videos = state.videos,
                    latestDownloadVideoIndex = state.latestDownloadVideoIndex,
                    downloading = state.downloading,
                    progress = state.progress,
                    onDownloadAll = viewModel::downloadAll,
                    onClearAll = viewModel::clearAll,
                    onClearCompleted = viewModel::clearCompleted,
                    onDeleteVideo = { viewModel.deleteVideo(it) },
                ) else EmptyQueue()
            }

        }
    }
}