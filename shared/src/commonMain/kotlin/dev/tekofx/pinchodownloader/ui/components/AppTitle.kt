package dev.tekofx.pinchodownloader.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.onClick
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import pinchodownloader.shared.generated.resources.Res
import pinchodownloader.shared.generated.resources.icon

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppTitle(
    onIconClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Image(
            painter = painterResource(Res.drawable.icon), contentDescription = null,
            Modifier.size(40.dp).onClick(onClick = onIconClick)
        )
        Text("Pincho Downloader", style = MaterialTheme.typography.displaySmall)
        UpdateTag()
    }
}