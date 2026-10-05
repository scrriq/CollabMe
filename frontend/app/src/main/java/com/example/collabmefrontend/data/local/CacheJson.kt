package com.example.collabmefrontend.data.local

import com.example.collabmefrontend.data.local.entity.ApplicationEntity
import com.example.collabmefrontend.data.local.entity.FavoriteApplicationEntity
import com.example.collabmefrontend.data.local.entity.UserProfileEntity
import com.example.collabmefrontend.data.remote.dto.ApplicationDto
import com.example.collabmefrontend.data.remote.dto.ApplicationTitledRefDto
import com.example.collabmefrontend.data.remote.dto.ApplicationThemeRefDto
import com.example.collabmefrontend.data.remote.dto.UserProfileDto
import com.example.collabmefrontend.domain.model.ApplicationItem
import com.example.collabmefrontend.domain.model.UserProfile
import kotlinx.serialization.json.Json

object CacheJson {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun userProfileToEntity(profile: UserProfile, isMine: Boolean): UserProfileEntity {
        return UserProfileEntity(
            userId = profile.userId,
            payload = json.encodeToString(UserProfileDto.serializer(), profile.toDto()),
            isMine = isMine,
            cachedAt = System.currentTimeMillis(),
        )
    }

    fun entityToUserProfile(entity: UserProfileEntity): UserProfile {
        return json.decodeFromString(UserProfileDto.serializer(), entity.payload).toDomain()
    }

    fun applicationToEntity(item: ApplicationItem): ApplicationEntity {
        return ApplicationEntity(
            id = item.id,
            payload = json.encodeToString(ApplicationDto.serializer(), item.toDto()),
            cachedAt = System.currentTimeMillis(),
        )
    }

    fun entityToApplication(entity: ApplicationEntity): ApplicationItem {
        return json.decodeFromString(ApplicationDto.serializer(), entity.payload).toDomain()
    }

    fun favoriteToEntity(item: ApplicationItem): FavoriteApplicationEntity {
        return FavoriteApplicationEntity(
            id = item.id,
            payload = json.encodeToString(ApplicationDto.serializer(), item.toDto()),
            cachedAt = System.currentTimeMillis(),
        )
    }

    fun entityToFavorite(entity: FavoriteApplicationEntity): ApplicationItem {
        return json.decodeFromString(ApplicationDto.serializer(), entity.payload).toDomain()
    }

    private fun UserProfile.toDto(): UserProfileDto {
        return UserProfileDto(
            userId = userId,
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
            updatedAt = updatedAt,
        )
    }

    private fun UserProfileDto.toDomain(): UserProfile {
        return UserProfile(
            userId = userId,
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
            updatedAt = updatedAt,
        )
    }

    private fun ApplicationItem.toDto(): ApplicationDto {
        return ApplicationDto(
            id = id,
            userId = userId,
            themeId = themeId,
            kindId = kindId,
            statusId = statusId,
            theme = ApplicationThemeRefDto(id = theme.id, name = theme.name),
            kind = ApplicationTitledRefDto(id = kind.id, title = kind.title),
            status = ApplicationTitledRefDto(id = status.id, title = status.title),
            title = title,
            description = description,
            createdAt = createdAt,
            updatedAt = updatedAt,
            completedAt = completedAt,
            deletedAt = deletedAt,
        )
    }
}
