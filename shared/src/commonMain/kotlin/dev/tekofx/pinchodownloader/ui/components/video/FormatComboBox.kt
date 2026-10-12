package dev.tekofx.pinchodownloader.ui.components.video

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
                val isSelected = ytDlpFormat.formatId == selectedFormat?.formatId

                DropdownMenuItem(
                    text = { Text(text = labelText(ytDlpFormat)) },
                    onClick = {
                        expanded = false
                        onFormatSelected(ytDlpFormat)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    // 💡 Agregamos un icono de confirmación al lado izquierdo si está seleccionado
                    trailingIcon = if (isSelected) {
                        {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else null,
                    // 💡 Modificamos el color del texto de forma segura sin parámetros de fondo erróneos
                    colors = MenuDefaults.itemColors(
                        textColor = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                )
            }
        }
    }
}