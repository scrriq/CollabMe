package com.example.collabmefrontend.presentation.applications.detail

import com.example.collabmefrontend.domain.model.ApplicationItem


data class ApplicationDetailState(
    val application: ApplicationItem? = null,
    val authorDisplayName: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed interface ApplicationDetailIntent {
    data class Load(val applicationId: String) : ApplicationDetailIntent
    data object Retry : ApplicationDetailIntent
    // Новый интент для клика по пользователю
    data class UserClicked(val userId: String) : ApplicationDetailIntent
}

sealed interface ApplicationDetailEffect {
    // Эффект для навигации
    data class NavigateToUserProfile(val userId: String) : ApplicationDetailEffect
}