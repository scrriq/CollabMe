package com.example.collabmefrontend.presentation.applications.favorites

import com.example.collabmefrontend.domain.model.ApplicationItem

data class FavoriteApplicationsState(
    val applications: List<ApplicationItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed interface FavoriteApplicationsIntent {
    data object Load : FavoriteApplicationsIntent
    data object Retry : FavoriteApplicationsIntent
    data class ApplicationClicked(val applicationId: String) : FavoriteApplicationsIntent
}

sealed interface FavoriteApplicationsEffect {
    data class NavigateToDetails(val applicationId: String) : FavoriteApplicationsEffect
}
