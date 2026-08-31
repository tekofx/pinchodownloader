package dev.tekofx.pinchodownloader

actual suspend fun downloadYtDlp(
    url: String,
    outDir: String,
    onProgress: (Double) -> Unit
) {
}