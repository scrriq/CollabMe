package com.example.collabmefrontend.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject

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
    val about: String? = null,
    val avatarUrl: String? = null,
    val socialLinks: JsonObject,
    val updatedAt: String
)

@Serializable
data class UserProfilePutRequestDto(
    val firstName: String,
    val lastName: String,
    val middleName: String? = null,
    val birthDate: String? = null,
    val gender: String? = null,
    val cityId: String? = null,
    val universityId: String? = null,
    val about: String? = null,
    val avatarUrl: String? = null,
    val socialLinks: JsonObject = buildJsonObject { }
)

@Serializable
data class UserProfilePatchRequestDto(
    val firstName: String? = null,
    val lastName: String? = null,
    val middleName: String? = null,
    val birthDate: String? = null,
    val gender: String? = null,
    val cityId: String? = null,
    val universityId: String? = null,
    val about: String? = null,
    val avatarUrl: String? = null,
    val socialLinks: JsonObject? = null
)