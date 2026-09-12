package dev.tekofx.pinchodownloader.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*

@Composable
fun UrlInputRow(
    url: String,
    onUrlChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onPaste: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = url,
            onValueChange = onUrlChange,
            label = { Text("Video URL") },
            singleLine = true,
            modifier = Modifier
                .weight(1f)
                .onPreviewKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown && event.key == Key.Enter) {
                        if (url.isNotBlank()) onSubmit()
                        true
                    } else false
                }
        )
        IconButton(onClick = onPaste) {
            Icon(Icons.Filled.ContentPaste, contentDescription = null)
        }
    }
}