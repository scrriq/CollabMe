package com.example.collabmefrontend.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApplicationResponseStatusDto(
    val responded: Boolean,
)
