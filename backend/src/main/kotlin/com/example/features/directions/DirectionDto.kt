package com.example.features.directions

import kotlinx.serialization.Serializable

@Serializable
data class DirectionDto(
    val id: String,
    val name: String,
)
