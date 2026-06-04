package com.example.collabmefrontend.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class ApplicationResponseUserDto(
    val userId: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val middleName: String? = null,
    val socialLinks: JsonObject,
    val respondedAt: String,
)
