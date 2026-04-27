package com.example.collabmefrontend.presentation.applications.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collabmefrontend.data.repository.ApplicationRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ApplicationsViewModel(
    private val applicationRepository: ApplicationRepository
): ViewModel(){
    private val _state = MutableStateFlow(ApplicationsState())
    val state: StateFlow<ApplicationsState> = _state.asStateFlow()

    private val _effect = Channel<ApplicationEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()


    fun onIntent(intent: ApplicationsIntent){
        when(intent){
            ApplicationsIntent.Load,
            ApplicationsIntent.Retry -> loadApplications()

            is ApplicationsIntent.ApplicationClicked -> {
                viewModelScope.launch {
                    _effect.send(ApplicationEffect.NavigateToDetails(intent.applicationId))
                }
            }
        }
    }

    private fun loadApplications(){
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            try{
                val items = applicationRepository.getApplications()
                _state.value = _state.value.copy(
                    applications = items,
                    isLoading = false,
                    error = null
                )
            }catch (e: Exception){
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load applications"
                )
            }
        }
    }
}