package com.example.features.applicationstatuses

import kotlinx.serialization.Serializable

@Serializable
data class ApplicationStatusDto(
    val id: String,
    val code: String,
    val title: String,
)
