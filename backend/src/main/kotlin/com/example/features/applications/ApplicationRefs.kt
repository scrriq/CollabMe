package com.example.features.applications

import kotlinx.serialization.Serializable

@Serializable
data class ApplicationThemeRef(
    val id: String,
    val name: String,
)

@Serializable
data class ApplicationTitledRef(
    val id: String,
    val title: String,
)
