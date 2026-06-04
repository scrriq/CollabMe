package com.example.collabmefrontend.domain.model

import kotlinx.serialization.json.JsonObject

data class UserProfile(
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
    val updatedAt: String
)
