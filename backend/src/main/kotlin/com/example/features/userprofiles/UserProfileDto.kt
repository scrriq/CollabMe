package com.example.features.userprofiles

import kotlinx.serialization.Serializable

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
    val socialLinks: String,
    val updatedAt: String,
)
