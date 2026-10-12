package dev.tekofx.pinchodownloader


import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.compose.resources.painterResource
import pinchodownloader.shared.generated.resources.Res
import pinchodownloader.shared.generated.resources.icon
import java.awt.Dimension

fun main() = application {

    Window(
        onCloseRequest = ::exitApplication,
        title = "Pincho Downloader",
        icon = painterResource(Res.drawable.icon)
    ) {
        val density = LocalDensity.current

        val minWidth = 800.dp
        val minHeight = 600.dp

        LaunchedEffect(density) {
            window.minimumSize = with(density) {
                Dimension(
                    minWidth.roundToPx(),
                    minHeight.roundToPx()
                )
            }
        }

        App()
    }
}

