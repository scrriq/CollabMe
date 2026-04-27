package com.example.collabmefrontend.presentation.applications.detail

import com.example.collabmefrontend.domain.model.ApplicationItem

data class ApplicationDetailState(
    val application: ApplicationItem? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed interface ApplicationDetailIntent{
    data class Load(val applicationId: String) : ApplicationDetailIntent
    data object Retry: ApplicationDetailIntent
}
