package com.example.collabmefrontend.data.repository

import com.example.collabmefrontend.data.remote.api.ProfileApi
import com.example.collabmefrontend.data.remote.api.ProfileNotFoundException
import com.example.collabmefrontend.data.remote.dto.UserProfilePatchRequestDto
import com.example.collabmefrontend.data.remote.dto.UserProfilePutRequestDto
import com.example.collabmefrontend.data.storage.TokenStorage
import com.example.collabmefrontend.domain.model.UserProfile
import kotlinx.serialization.json.JsonObject

class RemoteProfileRepository(
    private val api: ProfileApi,
    private val tokenStorage: TokenStorage
) : ProfileRepository {
    override suspend fun getMyProfileOrNull(): UserProfile? {
        val token = tokenStorage.getToken()
            ?: throw IllegalStateException("No access token")

        return try {
            api.getMyProfile(token).toDomain()
        } catch (_: ProfileNotFoundException) {
            null
        }
    }

    override suspend fun getProfileByUserId(userId: String): UserProfile? {
        return try{
            api.getProfileByUserId(userId).toDomain()
        } catch(_: ProfileNotFoundException){
            null
        }
    }

    override suspend fun createMyProfile(
        firstName: String,
        lastName: String,
        middleName: String?,
        birthDate: String?,
        gender: String?,
        cityId: String?,
        universityId: String?,
        about: String?,
        avatarUrl: String?,
        socialLinks: JsonObject
    ): UserProfile {
        val token = tokenStorage.getToken()
            ?: throw IllegalStateException("No access token")

        val response = api.putMyProfile(
            token = token,
            request = UserProfilePutRequestDto(
                firstName = firstName,
                lastName = lastName,
                middleName = middleName,
                birthDate = birthDate,
                gender = gender,
                cityId = cityId,
                universityId = universityId,
                about = about,
                avatarUrl = avatarUrl,
                socialLinks = socialLinks
            )
        )

        return response.toDomain()
    }

    override suspend fun updateMyProfile(
        firstName: String,
        lastName: String,
        middleName: String?,
        birthDate: String?,
        gender: String?,
        cityId: String?,
        universityId: String?,
        about: String?,
        avatarUrl: String?,
        socialLinks: JsonObject
    ): UserProfile {
        val token = tokenStorage.getToken()
            ?: throw IllegalStateException("No access token")

        val response = api.patchMyProfile(
            token = token,
            request = UserProfilePatchRequestDto(
                firstName = firstName,
                lastName = lastName,
                middleName = middleName,
                birthDate = birthDate,
                gender = gender,
                cityId = cityId,
                universityId = universityId,
                about = about,
                avatarUrl = avatarUrl,
                socialLinks = socialLinks
            )
        )

        return response.toDomain()
    }

    private fun com.example.collabmefrontend.data.remote.dto.UserProfileDto.toDomain(): UserProfile {
        return UserProfile(
            userId = userId,
            firstName = firstName,
            lastName = lastName,
            middleName = middleName,
            birthDate = birthDate,
            gender = gender,
            cityId = cityId,
            universityId = universityId,
            about = about,
            avatarUrl = avatarUrl,
            socialLinks = socialLinks,
            updatedAt = updatedAt
        )
    }
}