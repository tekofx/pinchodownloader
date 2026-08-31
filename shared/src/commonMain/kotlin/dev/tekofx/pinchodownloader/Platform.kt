package dev.tekofx.pinchodownloader

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform