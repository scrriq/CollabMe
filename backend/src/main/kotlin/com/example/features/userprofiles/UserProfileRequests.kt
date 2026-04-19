package com.example.features.userprofiles

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

// Структура добавления/обновления информации профиля пользователя

@Serializable
data class UserProfilePutRequest(
    val firstName: String,
    val lastName: String,
    val middleName: String? = null,
    val birthDate: String? = null,
    val gender: String? = null,
    val cityId: String? = null,
    val universityId: String? = null,
    val about: String? = null,
    val avatarUrl: String? = null,
    val socialLinks: JsonObject,
)

@Serializable
data class UserProfilePatchRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val middleName: String? = null,
    val birthDate: String? = null,
    val gender: String? = null,
    val cityId: String? = null,
    val universityId: String? = null,
    val about: String? = null,
    val avatarUrl: String? = null,
    val socialLinks: JsonObject? = null,
)
