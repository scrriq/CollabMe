package com.example.features.userprofiles

import com.example.app.plugins.NotFoundException
import com.example.app.plugins.ValidationException
import com.example.features.directions.DirectionsRepository
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.util.UUID

class UserProfilesService(
    private val userProfilesRepository: UserProfilesRepository,
    private val directionsRepository: DirectionsRepository,
) {
    fun getByUserId(userId: UUID): UserProfileDto? {
        return userProfilesRepository.findByUserId(userId)
    }

    fun put(userId: UUID, request: UserProfilePutRequest): UserProfileDto {
        if (request.firstName.isBlank() || request.lastName.isBlank()) {
            throw ValidationException("firstName and lastName are required")
        }

        val birthDate = request.birthDate?.takeIf { it.isNotBlank() }?.let { parseLocalDate(it) }
        val cityId = request.cityId?.takeIf { it.isNotBlank() }?.let { parseUuid(it, "cityId") }
        val universityId = request.universityId?.takeIf { it.isNotBlank() }?.let { parseUuid(it, "universityId") }
        val directionId = request.directionId?.takeIf { it.isNotBlank() }?.let { parseDirectionId(it) }
        val gender = request.gender?.trim()?.ifBlank { null }?.let(::validateGender)

        return userProfilesRepository.upsert(
            userId = userId,
            firstName = request.firstName.trim(),
            lastName = request.lastName.trim(),
            middleName = request.middleName?.trim()?.ifBlank { null },
            birthDate = birthDate,
            gender = gender,
            cityId = cityId,
            universityId = universityId,
            directionId = directionId,
            about = request.about?.trim()?.ifBlank { null },
            avatarUrl = request.avatarUrl?.trim()?.ifBlank { null },
            socialLinks = request.socialLinks,
        )
    }

    fun patch(userId: UUID, request: UserProfilePatchRequest): UserProfileDto {
        val ops = resolvePatchOps(request)
        if (!ops.hasAnyTouch()) {
            throw ValidationException("No fields to update")
        }
        return userProfilesRepository.patch(userId, ops)
            ?: throw NotFoundException("User profile not found")
    }

    private fun resolvePatchOps(request: UserProfilePatchRequest): UserProfilePatchOps {
        var ops = UserProfilePatchOps()

        if (request.firstName != null) {
            if (request.firstName.isBlank()) {
                throw ValidationException("firstName cannot be blank")
            }
            ops = ops.copy(firstName = request.firstName.trim(), touchFirstName = true)
        }
        if (request.lastName != null) {
            if (request.lastName.isBlank()) {
                throw ValidationException("lastName cannot be blank")
            }
            ops = ops.copy(lastName = request.lastName.trim(), touchLastName = true)
        }
        if (request.middleName != null) {
            ops = ops.copy(middleName = request.middleName.trim().ifBlank { null }, touchMiddleName = true)
        }
        if (request.birthDate != null) {
            val date =
                request.birthDate.takeIf { it.isNotBlank() }?.let { parseLocalDate(it) }
            ops = ops.copy(birthDate = date, touchBirthDate = true)
        }
        if (request.gender != null) {
            val gender = request.gender.trim().ifBlank { null }?.let(::validateGender)
            ops = ops.copy(gender = gender, touchGender = true)
        }
        if (request.cityId != null) {
            val id = request.cityId.takeIf { it.isNotBlank() }?.let { parseUuid(it, "cityId") }
            ops = ops.copy(cityId = id, touchCityId = true)
        }
        if (request.universityId != null) {
            val id = request.universityId.takeIf { it.isNotBlank() }?.let { parseUuid(it, "universityId") }
            ops = ops.copy(universityId = id, touchUniversityId = true)
        }
        if (request.directionId != null) {
            val id = request.directionId.takeIf { it.isNotBlank() }?.let { parseDirectionId(it) }
            ops = ops.copy(directionId = id, touchDirectionId = true)
        }
        if (request.about != null) {
            ops = ops.copy(about = request.about.trim().ifBlank { null }, touchAbout = true)
        }
        if (request.avatarUrl != null) {
            ops = ops.copy(avatarUrl = request.avatarUrl.trim().ifBlank { null }, touchAvatarUrl = true)
        }
        if (request.socialLinks != null) {
            ops = ops.copy(socialLinks = request.socialLinks, touchSocialLinks = true)
        }

        return ops
    }

    private fun parseLocalDate(raw: String): LocalDate =
        try {
            LocalDate.parse(raw)
        } catch (_: DateTimeParseException) {
            throw ValidationException("Invalid date format for birthDate (expected ISO-8601, e.g. 2001-05-20)")
        }

    private fun parseUuid(raw: String, field: String): UUID =
        try {
            UUID.fromString(raw)
        } catch (_: IllegalArgumentException) {
            throw ValidationException("Invalid UUID for $field")
        }

    private fun parseDirectionId(raw: String): UUID {
        val id = parseUuid(raw, "directionId")
        if (directionsRepository.findById(id) == null) {
            throw ValidationException("directionId not found")
        }
        return id
    }

    private fun validateGender(gender: String): String {
        if (gender != "male" && gender != "female") {
            throw ValidationException("gender must be one of: male, female")
        }
        return gender
    }
}
