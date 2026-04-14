package com.example.features.cities

import kotlinx.serialization.Serializable

@Serializable
data class CityDto(
    val id: String,
    val name: String,
)
