package com.example.collabmefrontend.presentation.applications.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collabmefrontend.data.repository.ApplicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class ApplicationDetailViewModel(
    private val applicationRepository: ApplicationRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ApplicationDetailState())
    val state: StateFlow<ApplicationDetailState> = _state.asStateFlow()

    // Добавляем канал эффектов
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
            // Обработка клика
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
            try{
                val application = applicationRepository.getApplicationById(applicationId)
                _state.value = _state.value.copy(
                    application = application,
                    isLoading = false,
                    error = null
                )
            }catch (e: Exception){
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load application"
                )
            }
        }
    }
}

