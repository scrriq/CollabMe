package com.example.collabmefrontend.presentation.applications.responders

data class ResponderContactUiModel(
    val platform: String,
    val url: String,
)

data class ResponderItemUiModel(
    val userId: String,
    val displayName: String,
    val respondedAt: String,
    val contacts: List<ResponderContactUiModel>,
)

data class ApplicationRespondersState(
    val responders: List<ResponderItemUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed interface ApplicationRespondersIntent {
    data class Load(val applicationId: String) : ApplicationRespondersIntent
    data object Retry : ApplicationRespondersIntent
    data class UserClicked(val userId: String) : ApplicationRespondersIntent
}

sealed interface ApplicationRespondersEffect {
    data class NavigateToUserProfile(val userId: String) : ApplicationRespondersEffect
}
