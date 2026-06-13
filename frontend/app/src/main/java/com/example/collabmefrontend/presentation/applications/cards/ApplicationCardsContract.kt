package com.example.collabmefrontend.presentation.applications.cards

import com.example.collabmefrontend.domain.model.ApplicationItem

data class ApplicationCardUiModel(
    val application: ApplicationItem,
    val authorFirstName: String,
    val authorLastName: String,
    val authorInitials: String,
    val authorAge: String,
    val universityLabel: String,
    val directionLabel: String,
)

data class ApplicationCardsState(
    val cards: List<ApplicationCardUiModel> = emptyList(),
    val currentIndex: Int = 0,
    val isLoading: Boolean = false,
    val isActionInProgress: Boolean = false,
    val error: String? = null,
) {
    val currentCard: ApplicationCardUiModel?
        get() = cards.getOrNull(currentIndex)

    val allViewed: Boolean
        get() = !isLoading && error == null && (cards.isEmpty() || currentIndex >= cards.size)
}

sealed interface ApplicationCardsIntent {
    data object Load : ApplicationCardsIntent
    data object Retry : ApplicationCardsIntent
    data object RejectClicked : ApplicationCardsIntent
    data object AcceptClicked : ApplicationCardsIntent
    data object ViewApplicationClicked : ApplicationCardsIntent
}

sealed interface ApplicationCardsEffect {
    data class NavigateToDetails(val applicationId: String) : ApplicationCardsEffect
    data class ShowMessage(val message: String) : ApplicationCardsEffect
}
