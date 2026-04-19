package com.example.features.userprofiles

import java.time.LocalDate
import java.util.UUID
import kotlinx.serialization.json.JsonObject

// "PATCH не поддерживает очистку полей (null)"

data class UserProfilePatchOps(
    val firstName: String? = null,
    val lastName: String? = null,
    val middleName: String? = null,
    val birthDate: LocalDate? = null,
    val gender: String? = null,
    val cityId: UUID? = null,
    val universityId: UUID? = null,
    val about: String? = null,
    val avatarUrl: String? = null,
    val socialLinks: JsonObject? = null,
    val touchFirstName: Boolean = false,
    val touchLastName: Boolean = false,
    val touchMiddleName: Boolean = false,
    val touchBirthDate: Boolean = false,
    val touchGender: Boolean = false,
    val touchCityId: Boolean = false,
    val touchUniversityId: Boolean = false,
    val touchAbout: Boolean = false,
    val touchAvatarUrl: Boolean = false,
    val touchSocialLinks: Boolean = false,
) {
    fun hasAnyTouch(): Boolean =
        touchFirstName ||
            touchLastName ||
            touchMiddleName ||
            touchBirthDate ||
            touchGender ||
            touchCityId ||
            touchUniversityId ||
            touchAbout ||
            touchAvatarUrl ||
            touchSocialLinks
}
