package com.example.collabmefrontend.presentation.applications.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collabmefrontend.data.repository.ApplicationRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class FavoriteApplicationsViewModel(
    private val applicationRepository: ApplicationRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(FavoriteApplicationsState())
    val state: StateFlow<FavoriteApplicationsState> = _state.asStateFlow()

    private val _effect = Channel<FavoriteApplicationsEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: FavoriteApplicationsIntent) {
        when (intent) {
            FavoriteApplicationsIntent.Load,
            FavoriteApplicationsIntent.Retry -> load()
            is FavoriteApplicationsIntent.ApplicationClicked -> {
                viewModelScope.launch {
                    _effect.send(FavoriteApplicationsEffect.NavigateToDetails(intent.applicationId))
                }
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val items = applicationRepository.getMyResponseApplications()
                _state.value = _state.value.copy(
                    applications = items,
                    isLoading = false,
                    error = null,
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Не удалось загрузить избранное",
                )
            }
        }
    }
}
