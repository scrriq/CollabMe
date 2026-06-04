package com.example.collabmefrontend.presentation.applications.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collabmefrontend.core.util.buildAuthorDisplayName
import com.example.collabmefrontend.data.repository.ApplicationRepository
import com.example.collabmefrontend.data.repository.ProfileRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ApplicationDetailViewModel(
    private val applicationRepository: ApplicationRepository,
    private val profileRepository: ProfileRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(ApplicationDetailState())
    val state: StateFlow<ApplicationDetailState> = _state.asStateFlow()

    private val _effect = Channel<ApplicationDetailEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var currentApplicationId: String? = null

    fun onIntent(intent: ApplicationDetailIntent) {
        when (intent) {
            is ApplicationDetailIntent.Load -> {
                currentApplicationId = intent.applicationId
                loadApplication(intent.applicationId)
            }
            ApplicationDetailIntent.Retry -> {
                val id = currentApplicationId ?: return
                loadApplication(id)
            }
            ApplicationDetailIntent.RespondClicked -> respond()
            ApplicationDetailIntent.WithdrawClicked -> withdraw()
            is ApplicationDetailIntent.UserClicked -> {
                viewModelScope.launch {
                    _effect.send(ApplicationDetailEffect.NavigateToUserProfile(intent.userId))
                }
            }
        }
    }

    private fun loadApplication(applicationId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val application = applicationRepository.getApplicationById(applicationId)
                val currentUserId = runCatching {
                    profileRepository.getMyProfileOrNull()?.userId
                }.getOrNull()
                val profile = runCatching {
                    profileRepository.getProfileByUserId(application.userId)
                }.getOrNull()
                val authorName = profile?.let {
                    buildAuthorDisplayName(it.firstName, it.lastName)
                }.orEmpty().ifBlank { null }
                val isOwner = currentUserId != null && currentUserId == application.userId
                val hasResponded = if (currentUserId != null && !isOwner) {
                    runCatching { applicationRepository.hasResponded(applicationId) }.getOrDefault(false)
                } else {
                    false
                }

                _state.value = _state.value.copy(
                    application = application,
                    authorDisplayName = authorName,
                    currentUserId = currentUserId,
                    isOwner = isOwner,
                    hasResponded = hasResponded,
                    isLoading = false,
                    error = null,
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Не удалось загрузить заявку",
                )
            }
        }
    }

    private fun respond() {
        val applicationId = currentApplicationId ?: return
        if (_state.value.isOwner || _state.value.hasResponded || _state.value.isResponseUpdating) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isResponseUpdating = true, error = null)
            try {
                applicationRepository.respondToApplication(applicationId)
                _state.value = _state.value.copy(
                    hasResponded = true,
                    isResponseUpdating = false,
                )
                _effect.send(ApplicationDetailEffect.ShowMessage("Заявка добавлена в избранное"))
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isResponseUpdating = false,
                    error = e.message ?: "Не удалось откликнуться",
                )
            }
        }
    }

    private fun withdraw() {
        val applicationId = currentApplicationId ?: return
        if (_state.value.isOwner || !_state.value.hasResponded || _state.value.isResponseUpdating) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isResponseUpdating = true, error = null)
            try {
                applicationRepository.withdrawResponse(applicationId)
                _state.value = _state.value.copy(
                    hasResponded = false,
                    isResponseUpdating = false,
                )
                _effect.send(ApplicationDetailEffect.ShowMessage("Заявка убрана из избранного"))
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isResponseUpdating = false,
                    error = e.message ?: "Не удалось убрать из избранного",
                )
            }
        }
    }
}
