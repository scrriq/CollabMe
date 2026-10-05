package com.example.collabmefrontend.data.repository

import com.example.collabmefrontend.data.local.CacheJson
import com.example.collabmefrontend.data.local.dao.ApplicationDao
import com.example.collabmefrontend.data.local.dao.FavoriteApplicationDao
import com.example.collabmefrontend.data.local.dao.UserProfileDao
import com.example.collabmefrontend.data.remote.dto.ApplicationCreateRequest
import com.example.collabmefrontend.data.remote.dto.ApplicationPatchRequest
import com.example.collabmefrontend.data.remote.dto.ApplicationResponseUserDto
import com.example.collabmefrontend.domain.model.ApplicationItem
import com.example.collabmefrontend.domain.model.UserProfile
import kotlinx.serialization.json.JsonObject

class CachedProfileRepository(
    private val remote: RemoteProfileRepository,
    private val userProfileDao: UserProfileDao,
) : ProfileRepository {

    override suspend fun getMyProfileOrNull(): UserProfile? {
        return try {
            val profile = remote.getMyProfileOrNull()
            if (profile != null) {
                userProfileDao.upsert(CacheJson.userProfileToEntity(profile, isMine = true))
            }
            profile
        } catch (_: Exception) {
            userProfileDao.getMyProfile()?.let(CacheJson::entityToUserProfile)
        }
    }

    override suspend fun getProfileByUserId(userId: String): UserProfile? {
        return try {
            val profile = remote.getProfileByUserId(userId)
            if (profile != null) {
                userProfileDao.upsert(CacheJson.userProfileToEntity(profile, isMine = false))
            }
            profile
        } catch (_: Exception) {
            userProfileDao.getByUserId(userId)?.let(CacheJson::entityToUserProfile)
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
        directionId: String?,
        about: String?,
        avatarUrl: String?,
        socialLinks: JsonObject,
    ): UserProfile {
        val profile = remote.createMyProfile(
            firstName = firstName,
            lastName = lastName,
            middleName = middleName,
            birthDate = birthDate,
            gender = gender,
            cityId = cityId,
            universityId = universityId,
            directionId = directionId,
            about = about,
            avatarUrl = avatarUrl,
            socialLinks = socialLinks,
        )
        userProfileDao.upsert(CacheJson.userProfileToEntity(profile, isMine = true))
        return profile
    }

    override suspend fun updateMyProfile(
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
        socialLinks: JsonObject,
    ): UserProfile {
        val profile = remote.updateMyProfile(
            firstName = firstName,
            lastName = lastName,
            middleName = middleName,
            birthDate = birthDate,
            gender = gender,
            cityId = cityId,
            universityId = universityId,
            directionId = directionId,
            about = about,
            avatarUrl = avatarUrl,
            socialLinks = socialLinks,
        )
        userProfileDao.upsert(CacheJson.userProfileToEntity(profile, isMine = true))
        return profile
    }
}
