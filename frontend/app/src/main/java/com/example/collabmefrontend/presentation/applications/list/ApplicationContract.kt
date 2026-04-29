package com.example.collabmefrontend.presentation.applications.list

import com.example.collabmefrontend.domain.model.ApplicationItem

data class ApplicationsState(
    val applications: List<ApplicationItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface ApplicationsIntent {
    data object Load : ApplicationsIntent
    data object Retry : ApplicationsIntent
    data class ApplicationClicked(val applicationId: String) : ApplicationsIntent
    data object AddClicked : ApplicationsIntent
}

sealed interface ApplicationEffect {
    data class NavigateToDetails(val applicationId: String) : ApplicationEffect
    data object NavigateToCreate : ApplicationEffect
}