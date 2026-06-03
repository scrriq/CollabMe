package com.example.features.applications

import kotlinx.serialization.Serializable

@Serializable
data class ApplicationDto(
    val id: String,
    val userId: String,
    val themeId: String,
    val kindId: String,
    val statusId: String,
    val theme: ApplicationThemeRef,
    val kind: ApplicationTitledRef,
    val status: ApplicationTitledRef,
    val title: String,
    val description: String,
    val createdAt: String,
    val updatedAt: String,
    val completedAt: String? = null,
    val deletedAt: String? = null,
)
