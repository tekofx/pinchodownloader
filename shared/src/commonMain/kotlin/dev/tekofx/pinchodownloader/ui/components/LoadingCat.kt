package dev.tekofx.pinchodownloader.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.compottie.*
import pinchodownloader.shared.generated.resources.Res

@Composable
fun LoadingCat() {
    // 1. Cargamos el String del JSON de forma asíncrona usando produceState
    val jsonString by produceState<String?>(initialValue = null) {
        value = try {
            Res.readBytes("files/loading_cat.json").decodeToString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // 2. Pasamos el JSON al motor de Lottie una vez que se haya leído el archivo
    val composition: LottieComposition? = jsonString?.let { json ->
        val comp by rememberLottieComposition(spec = LottieCompositionSpec.JsonString(json))
        comp
    }

    // 3. Renderizamos la animación solo cuando la composición esté lista
    if (composition != null) {
        Image(
            painter = rememberLottiePainter(
                composition = composition,
                iterations = Compottie.IterateForever
            ),
            contentDescription = "Animated Emoji",
            modifier = Modifier.size(200.dp)
        )
    }
}