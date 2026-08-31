package dev.tekofx.pinchodownloader.entities

import androidx.compose.ui.graphics.Color

enum class TaskStatus(val label: String, val color: Color) {
    PENDING("Pending", Color(0xFFFFC107)),
    IN_PROGRESS("In Progress", Color(0xFF2196F3)),
    COMPLETED("Completed", Color(0xFF4CAF50))
}

