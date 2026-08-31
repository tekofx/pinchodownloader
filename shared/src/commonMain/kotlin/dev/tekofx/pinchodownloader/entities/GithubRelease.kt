package dev.tekofx.pinchodownloader.entities

import kotlinx.serialization.Serializable

@Serializable
data class GitHubRelease(
    val tag_name: String,
    val name: String
)