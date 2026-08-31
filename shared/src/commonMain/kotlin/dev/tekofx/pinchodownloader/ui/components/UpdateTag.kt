package dev.tekofx.pinchodownloader.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.onClick
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.tekofx.pinchodownloader.checkPinchoDownloaderUpdate
import dev.tekofx.pinchodownloader.checkYtDlpUpdate
import dev.tekofx.pinchodownloader.entities.UpdateState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UpdateTag(
    onClick: () -> Unit,
) {
    var newAppVersion by remember { mutableStateOf(UpdateState.Checking) }
    var newYtdlpVersion by remember { mutableStateOf(UpdateState.Checking) }

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
            modifier = Modifier.onClick(onClick = onClick),
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
}
