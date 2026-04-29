package com.example.collabmefrontend.presentation.applications.form

data class ApplicationFormState(
    val id: String? = null,
    val themeId: String = "",
    val kindId: String = "",
    val statusId: String = "",
    val title: String = "",
    val description: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
)

sealed interface ApplicationFormIntent {
    data class Load(val applicationId: String?) : ApplicationFormIntent
    data class TitleChanged(val value: String) : ApplicationFormIntent
    data class DescriptionChanged(val value: String) : ApplicationFormIntent
    data class ThemeChanged(val value: String) : ApplicationFormIntent
    data class KindChanged(val value: String) : ApplicationFormIntent
    data class StatusChanged(val value: String) : ApplicationFormIntent
    data object SaveClicked : ApplicationFormIntent
}

sealed interface ApplicationFormEffect {
    data object Saved : ApplicationFormEffect
    data class ShowMessage(val message: String) : ApplicationFormEffect
}