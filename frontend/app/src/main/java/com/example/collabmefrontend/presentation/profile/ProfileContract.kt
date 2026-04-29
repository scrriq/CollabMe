package com.example.collabmefrontend.presentation.profile

import com.example.collabmefrontend.domain.model.UserProfile
import com.example.collabmefrontend.data.repository.CatalogOption
import com.example.collabmefrontend.domain.model.SocialLinkUiModel


data class ProfileState(
    val user: UserProfile? = null,
    val firstName: String = "",
    val lastName: String = "",
    val middleName: String = "",
    val birthDate: String = "",
    val gender: String = "",
    val cityId: String = "",
    val universityId: String = "",
    val about: String = "",
    val avatarUrl: String = "",
    val socialLinks: List<SocialLinkUiModel> = listOf(SocialLinkUiModel()),
    val cityOptions: List<CatalogOption> = emptyList(),
    val universityOptions: List<CatalogOption> = emptyList(),
    val isFirstRegistration: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)

sealed interface ProfileIntent{
    data object Load: ProfileIntent
    data class FirstNameChanged(val value: String) : ProfileIntent
    data class LastNameChanged(val value: String) : ProfileIntent
    data class MiddleNameChanged(val value: String) : ProfileIntent
    data class BirthDateChanged(val value: String) : ProfileIntent
    data class GenderChanged(val value: String?) : ProfileIntent
    data class CityIdChanged(val value: String?) : ProfileIntent
    data class UniversityIdChanged(val value: String?) : ProfileIntent
    data class AboutChanged(val value: String) : ProfileIntent
    data class AvatarUrlChanged(val value: String) : ProfileIntent

    data class RemoveSocialLink(val index: Int) : ProfileIntent
    data class SocialPlatformChanged(val index: Int, val value: String) : ProfileIntent
    data class SocialUrlChanged(val index: Int, val value: String) : ProfileIntent

    data object AddSocialLink : ProfileIntent
    data object Save : ProfileIntent
    data object Logout: ProfileIntent
}

sealed interface ProfileEffect{
    data object NavigateToLogin : ProfileEffect
    data class NavigateToPublicProfile(val userId: String) : ProfileEffect
}