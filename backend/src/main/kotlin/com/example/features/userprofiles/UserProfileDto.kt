package com.example.features.userprofiles

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

// Ответ клиенту ( профиль пользователя )

@Serializable
data class UserProfileDto(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val middleName: String? = null,
    val birthDate: String? = null,
    val gender: String? = null,
    val cityId: String? = null,
    val universityId: String? = null,
    val directionId: String? = null,
    val about: String? = null,
    val avatarUrl: String? = null,
    val socialLinks: JsonObject,
    val updatedAt: String,
)
