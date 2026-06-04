package com.example.collabmefrontend.presentation.applications.detail

import com.example.collabmefrontend.domain.model.ApplicationItem

data class ApplicationDetailState(
    val application: ApplicationItem? = null,
    val authorDisplayName: String? = null,
    val currentUserId: String? = null,
    val isOwner: Boolean = false,
    val hasResponded: Boolean = false,
    val isResponseUpdating: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed interface ApplicationDetailIntent {
    data class Load(val applicationId: String) : ApplicationDetailIntent
    data object Retry : ApplicationDetailIntent
    data object RespondClicked : ApplicationDetailIntent
    data object WithdrawClicked : ApplicationDetailIntent
    data class UserClicked(val userId: String) : ApplicationDetailIntent
    data object ViewRespondersClicked : ApplicationDetailIntent
}

sealed interface ApplicationDetailEffect {
    data class NavigateToUserProfile(val userId: String) : ApplicationDetailEffect
    data class NavigateToResponders(val applicationId: String) : ApplicationDetailEffect
    data class ShowMessage(val message: String) : ApplicationDetailEffect
}
