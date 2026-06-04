package com.example.collabmefrontend.data.repository

import com.example.collabmefrontend.domain.model.UserProfile
import kotlinx.serialization.json.JsonObject

interface ProfileRepository {
    suspend fun getMyProfileOrNull(): UserProfile?
    suspend fun getProfileByUserId(userId: String) : UserProfile?

    suspend fun createMyProfile(
        firstName: String,
        lastName: String,
        middleName: String?,
        birthDate: String?,
        gender: String?,
        cityId: String?,
        universityId: String?,
        directionId: String?,
        about: String?,
        avatarUrl: String?,
        socialLinks: JsonObject
    ): UserProfile

    suspend fun updateMyProfile(
        firstName: String,
        lastName: String,
        middleName: String?,
        birthDate: String?,
        gender: String?,
        cityId: String?,
        universityId: String?,
        directionId: String?,
        about: String?,
        avatarUrl: String?,
        socialLinks: JsonObject
    ): UserProfile
}