package com.example.collabmefrontend.presentation.applications.cards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collabmefrontend.core.util.buildUserInitials
import com.example.collabmefrontend.core.util.calculateAgeText
import com.example.collabmefrontend.data.repository.ApplicationRepository
import com.example.collabmefrontend.data.repository.ProfileCatalogRepository
import com.example.collabmefrontend.data.repository.ProfileRepository
import com.example.collabmefrontend.domain.model.ApplicationItem
import com.example.collabmefrontend.domain.model.UserProfile
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ApplicationCardsViewModel(
    private val applicationRepository: ApplicationRepository,
    private val profileRepository: ProfileRepository,
    private val profileCatalogRepository: ProfileCatalogRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(ApplicationCardsState())
    val state: StateFlow<ApplicationCardsState> = _state.asStateFlow()

    private val _effect = Channel<ApplicationCardsEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: ApplicationCardsIntent) {
        when (intent) {
            ApplicationCardsIntent.Load,
            ApplicationCardsIntent.Retry -> load()
            ApplicationCardsIntent.RejectClicked -> showNextCard()
            ApplicationCardsIntent.AcceptClicked -> acceptCurrentCard()
            ApplicationCardsIntent.ViewApplicationClicked -> openCurrentCardDetails()
        }
    }

    private fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val allApplications = applicationRepository.getApplications()
                val respondedIds = applicationRepository.getMyResponseApplications()
                    .map { it.id }
                    .toSet()
                val currentUserId = profileRepository.getMyProfileOrNull()?.userId

                val universities = runCatching { profileCatalogRepository.getUniversities() }
                    .getOrDefault(emptyList())
                val directions = runCatching { profileCatalogRepository.getDirections() }
                    .getOrDefault(emptyList())

                val cards = allApplications
                    .filter { application -> currentUserId == null || application.userId != currentUserId }
                    .filter { application -> application.id !in respondedIds }
                    .map { application ->
                        buildCardUiModel(
                            application = application,
                            profile = runCatching {
                                profileRepository.getProfileByUserId(application.userId)
                            }.getOrNull(),
                            universities = universities.associate { it.id to it.label },
                            directions = directions.associate { it.id to it.label },
                        )
                    }

                _state.value = _state.value.copy(
                    cards = cards,
                    currentIndex = 0,
                    isLoading = false,
                    error = null,
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Не удалось загрузить карточки",
                )
            }
        }
    }

    private fun buildCardUiModel(
        application: ApplicationItem,
        profile: UserProfile?,
        universities: Map<String, String>,
        directions: Map<String, String>,
    ): ApplicationCardUiModel {
        val firstName = profile?.firstName.orEmpty()
        val lastName = profile?.lastName.orEmpty()
        val age = profile?.birthDate
            ?.takeIf { it.isNotBlank() }
            ?.let(::calculateAgeText)
            .orEmpty()

        return ApplicationCardUiModel(
            application = application,
            authorFirstName = firstName,
            authorLastName = lastName,
            authorInitials = buildUserInitials(firstName, lastName),
            authorAge = age,
            universityLabel = profile?.universityId?.let { universities[it] }.orEmpty(),
            directionLabel = profile?.directionId?.let { directions[it] }.orEmpty(),
        )
    }

    private fun showNextCard() {
        val state = _state.value
        if (state.isActionInProgress || state.currentCard == null) return
        _state.value = state.copy(currentIndex = state.currentIndex + 1)
    }

    private fun acceptCurrentCard() {
        val card = _state.value.currentCard ?: return
        if (_state.value.isActionInProgress) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isActionInProgress = true, error = null)
            try {
                applicationRepository.respondToApplication(card.application.id)
                _state.value = _state.value.copy(
                    isActionInProgress = false,
                    currentIndex = _state.value.currentIndex + 1,
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(isActionInProgress = false)
                _effect.send(
                    ApplicationCardsEffect.ShowMessage(
                        e.message ?: "Не удалось добавить в избранное",
                    ),
                )
            }
        }
    }

    private fun openCurrentCardDetails() {
        val applicationId = _state.value.currentCard?.application?.id ?: return
        viewModelScope.launch {
            _effect.send(ApplicationCardsEffect.NavigateToDetails(applicationId))
        }
    }
}
