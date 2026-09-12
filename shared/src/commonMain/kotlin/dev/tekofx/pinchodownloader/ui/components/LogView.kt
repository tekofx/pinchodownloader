package dev.tekofx.pinchodownloader.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.tekofx.pinchodownloader.log.LogStatus
import dev.tekofx.pinchodownloader.log.LogStore

@Composable
fun LogView(
    onCloseClick: () -> Unit,
) {
    val logs by LogStore.logs.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Logs") },
                actions = {
                    TextButton(onClick = { LogStore.clear() }) { Text("Clear") }
                    TextButton(onClick = { onCloseClick() }) { Text("Close") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            reverseLayout = true, // newest at top
            contentPadding = PaddingValues(8.dp)
        ) {
            items(logs) { log ->
                val statusColor = when (log.status) {
                    LogStatus.DEBUG -> Color.Green
                    LogStatus.INFO -> Color.Blue
                    LogStatus.WARN -> Color.Yellow
                    LogStatus.ERROR -> Color.Red
                    LogStatus.FATAL -> Color(0xFFFF4444)
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = log.time,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                    Text(
                        text = log.status.toString(),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = statusColor,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                    Text(
                        text = log.tag,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = Color.Cyan,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                    Text(
                        text = log.message,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}