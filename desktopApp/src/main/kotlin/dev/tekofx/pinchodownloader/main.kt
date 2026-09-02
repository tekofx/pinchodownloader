package dev.tekofx.pinchodownloader


import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.compose.resources.painterResource
import pinchodownloader.shared.generated.resources.Res
import pinchodownloader.shared.generated.resources.icon

fun main() = application {

    Window(
        onCloseRequest = ::exitApplication,
        title = "Pincho Downloader",
        icon = painterResource(Res.drawable.icon)
    ) {
        App()
    }
}

