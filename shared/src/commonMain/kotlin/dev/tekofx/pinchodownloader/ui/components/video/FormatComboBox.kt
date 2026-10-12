package dev.tekofx.pinchodownloader.ui.components.video

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import dev.tekofx.pinchodownloader.entities.VideoFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormatComboBox(
    modifier: Modifier = Modifier,
    formats: List<VideoFormat>,
    selectedFormat: VideoFormat?,
    onFormatSelected: (VideoFormat?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val labelText = { format: VideoFormat? ->
        when {
            format == null -> "No formats available"
            format.name.isNotBlank() -> format.name
            else -> "Format ID: ${format.formatId}"
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = labelText(selectedFormat),
            onValueChange = {},
            readOnly = true,
            label = { Text("Quality / Format") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            formats.forEach { ytDlpFormat ->
                DropdownMenuItem(
                    text = { Text(text = labelText(ytDlpFormat)) },
                    onClick = {
                        expanded = false // Cerramos el menú flotante
                        onFormatSelected(ytDlpFormat) // Notificamos al ViewModel el nuevo formato elegido
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}