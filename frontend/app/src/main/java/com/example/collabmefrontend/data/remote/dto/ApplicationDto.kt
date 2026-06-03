package com.example.collabmefrontend.data.remote.dto

import com.example.collabmefrontend.domain.model.ApplicationItem
import kotlinx.serialization.Serializable

@Serializable
data class ApplicationDto(
    val id: String,
    val userId: String,
    val themeId: String,
    val kindId: String,
    val statusId: String,
    val theme: ApplicationThemeRefDto,
    val kind: ApplicationTitledRefDto,
    val status: ApplicationTitledRefDto,
    val title: String,
    val description: String,
    val createdAt: String,
    val updatedAt: String,
    val completedAt: String? = null,
    val deletedAt: String? = null,
) {
    fun toDomain(): ApplicationItem {
        return ApplicationItem(
            id = id,
            userId = userId,
            themeId = themeId,
            kindId = kindId,
            statusId = statusId,
            theme = theme.toDomain(),
            kind = kind.toKindDomain(),
            status = status.toStatusDomain(),
            title = title,
            description = description,
            createdAt = createdAt,
            updatedAt = updatedAt,
            completedAt = completedAt,
            deletedAt = deletedAt,
        )
    }
}
