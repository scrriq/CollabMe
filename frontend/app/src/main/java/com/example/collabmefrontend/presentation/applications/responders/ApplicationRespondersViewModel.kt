package com.example.collabmefrontend.presentation.applications.responders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collabmefrontend.core.util.buildFullDisplayName
import com.example.collabmefrontend.core.util.formatApiDateTime
import com.example.collabmefrontend.data.remote.dto.ApplicationResponseUserDto
import com.example.collabmefrontend.data.repository.ApplicationRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonPrimitive

class ApplicationRespondersViewModel(
    private val applicationRepository: ApplicationRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(ApplicationRespondersState())
    val state: StateFlow<ApplicationRespondersState> = _state.asStateFlow()

    private val _effect = Channel<ApplicationRespondersEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var currentApplicationId: String? = null

    fun onIntent(intent: ApplicationRespondersIntent) {
        when (intent) {
            is ApplicationRespondersIntent.Load -> {
                currentApplicationId = intent.applicationId
                load(intent.applicationId)
            }
            ApplicationRespondersIntent.Retry -> {
                val id = currentApplicationId ?: return
                load(id)
            }
            is ApplicationRespondersIntent.UserClicked -> {
                viewModelScope.launch {
                    _effect.send(ApplicationRespondersEffect.NavigateToUserProfile(intent.userId))
                }
            }
        }
    }

    private fun load(applicationId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val responders = applicationRepository.listApplicationResponders(applicationId)
                    .map(::toUiModel)
                _state.value = _state.value.copy(
                    responders = responders,
                    isLoading = false,
                    error = null,
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Не удалось загрузить отклики",
                )
            }
        }
    }

    private fun toUiModel(dto: ApplicationResponseUserDto): ResponderItemUiModel {
        val displayName = buildFullDisplayName(dto.lastName, dto.firstName, dto.middleName)
            .ifBlank { "Пользователь" }
        val contacts = dto.socialLinks.entries.map { entry ->
            ResponderContactUiModel(
                platform = entry.key.replaceFirstChar { ch ->
                    if (ch.isLowerCase()) ch.titlecase() else ch.toString()
                },
                url = entry.value.jsonPrimitive.content,
            )
        }
        return ResponderItemUiModel(
            userId = dto.userId,
            displayName = displayName,
            respondedAt = formatApiDateTime(dto.respondedAt),
            contacts = contacts,
        )
    }
}
