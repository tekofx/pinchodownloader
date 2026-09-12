package dev.tekofx.pinchodownloader.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.tekofx.pinchodownloader.entities.ButtonVariant

@Composable
fun TextIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    text: String,
    variant: ButtonVariant = ButtonVariant.Filled,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
) {

    val content: @Composable RowScope.() -> Unit = {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(text)
    }

    when (variant) {
        ButtonVariant.Filled -> Button(onClick = onClick, colors = colors, content = content)
        ButtonVariant.Outlined -> OutlinedButton(onClick = onClick, colors = colors, content = content)
    }
}