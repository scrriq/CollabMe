package com.example.collabmefrontend.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApplicationCreateRequest(
    val themeId: String,
    val kindId: String,
    val statusId: String,
    val title: String,
    val description: String,
)

@Serializable
data class ApplicationPatchRequest(
    val themeId: String? = null,
    val kindId: String? = null,
    val statusId: String? = null,
    val title: String? = null,
    val description: String? = null,
)