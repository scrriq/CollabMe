package com.example.collabmefrontend.presentation.profile.publicprofile

import com.example.collabmefrontend.domain.model.UserProfile

data class PublicProfileContactUiModel(
    val platform: String,
    val url: String
)

data class PublicProfileState(
    val user: UserProfile? = null,
    val isOwnProfile: Boolean = false,
    val displayName: String = "",
    val ageText: String = "",
    val universityText: String = "",
    val professionText: String = "Android разработчик",
    val descriptionText: String = "",
    val contacts: List<PublicProfileContactUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface PublicProfileIntent {
    data class Load(
        val userId: String,
        val isOwnProfile: Boolean = false
    ) : PublicProfileIntent

    data object Retry : PublicProfileIntent
}

sealed interface PublicProfileEffect {
    data object NavigateBack : PublicProfileEffect
    data object NavigateToEdit : PublicProfileEffect
}