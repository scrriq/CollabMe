package com.example.features.applicationkinds

import kotlinx.serialization.Serializable

@Serializable
data class ApplicationKindDto(
    val id: String,
    val code: String,
    val title: String,
)
