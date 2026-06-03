package com.example.collabmefrontend.domain.model

data class ApplicationItem(
    val id: String,
    val userId: String,
    val themeId: String,
    val kindId: String,
    val statusId: String,
    val theme: ApplicationThemeRef,
    val kind: ApplicationKindRef,
    val status: ApplicationStatusRef,
    val title: String,
    val description: String,
    val createdAt: String,
    val updatedAt: String,
    val completedAt: String? = null,
    val deletedAt: String? = null,
)
