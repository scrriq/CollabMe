package com.example.collabmefrontend.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CityDto(
    val id: String,
    val name: String,
)

@Serializable
data class UniversityDto(
    val id: String,
    val name: String,
    val slug: String,
    val cityId: String? = null,
)
