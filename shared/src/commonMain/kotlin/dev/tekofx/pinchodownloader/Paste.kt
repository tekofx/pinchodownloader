package dev.tekofx.pinchodownloader

import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor

// Top-level or in a utility file
fun pasteFromClipboard(): String? = try {
    Toolkit.getDefaultToolkit().systemClipboard.getData(DataFlavor.stringFlavor) as? String
} catch (_: Exception) {
    null
}