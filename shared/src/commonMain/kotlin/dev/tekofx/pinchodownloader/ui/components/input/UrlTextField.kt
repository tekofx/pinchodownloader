package dev.tekofx.pinchodownloader.ui.components.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import dev.tekofx.pinchodownloader.pasteFromClipboard

@Composable
fun UrlTextField(
    url: String,
    onUrlChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TextField(
            shape = MaterialTheme.shapes.extraLarge,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unfocusedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
            value = url,
            onValueChange = onUrlChange,
            label = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Link, contentDescription = null)
                    Text("Video URL")
                }
            },
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
        IconButton(
            onClick = {
                pasteFromClipboard()?.let { onUrlChange(it) }
                onSubmit()
            },
            shape = RoundedCornerShape(12.dp),
            colors = IconButtonDefaults.filledIconButtonColors()
        ) {
            Icon(Icons.Filled.ContentPaste, contentDescription = null)
        }
    }
}