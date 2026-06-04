package com.example.features.applicationresponses

import kotlinx.serialization.Serializable

@Serializable
data class ApplicationResponseUserDto(
    val userId: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val respondedAt: String,
)

@Serializable
data class ApplicationResponseStatusDto(
    val responded: Boolean,
)
