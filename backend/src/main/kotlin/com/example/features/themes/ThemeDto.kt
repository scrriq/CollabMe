package com.example.features.themes

import kotlinx.serialization.Serializable

@Serializable
data class ThemeDto(
    val id: String,
    val name: String,
    val directionId: String? = null,
)
