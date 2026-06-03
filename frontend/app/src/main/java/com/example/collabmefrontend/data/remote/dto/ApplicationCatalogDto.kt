package com.example.collabmefrontend.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ThemeDto(
    val id: String,
    val name: String,
    val directionId: String? = null,
)

@Serializable
data class ApplicationKindDto(
    val id: String,
    val code: String,
    val title: String,
)

@Serializable
data class ApplicationStatusDto(
    val id: String,
    val code: String,
    val title: String,
)
