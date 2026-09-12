package dev.tekofx.pinchodownloader.filesystem

import java.io.File


actual fun getDownloadsDir(): String {
    val home = System.getProperty("user.home")
    val dir = File(home, "Downloads")
    if (!dir.exists()) dir.mkdirs()
    return dir.absolutePath
}