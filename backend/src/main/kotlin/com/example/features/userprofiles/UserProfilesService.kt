package com.example.features.userprofiles

import java.util.UUID

class UserProfilesService(
    private val userProfilesRepository: UserProfilesRepository
) {
    fun getByUserId(userId: UUID): UserProfileDto? {
        return userProfilesRepository.findByUserId(userId)
    }
}
