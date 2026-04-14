package com.example.features.universities

import kotlinx.serialization.Serializable

@Serializable
data class UniversityDto(
    val id: String,
    val name: String,
    val slug: String,
    val cityId: String? = null,
)
