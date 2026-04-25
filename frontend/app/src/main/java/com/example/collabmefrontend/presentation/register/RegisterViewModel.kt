package com.example.collabmefrontend.presentation.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collabmefrontend.data.remote.api.ApiException
import com.example.collabmefrontend.data.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    private val _effect = Channel<RegisterEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: RegisterIntent){
        when(intent){
            is RegisterIntent.LoginChanged -> {
                _state.value = _state.value.copy(login = intent.value, error = null)
            }
            is RegisterIntent.EmailChanged -> {
                _state.value = _state.value.copy(email = intent.value, error = null)
            }
            is RegisterIntent.PasswordChanged -> {
                _state.value = _state.value.copy(password = intent.value, error = null)
            }
            is RegisterIntent.PhoneChanged -> {
                _state.value = _state.value.copy(phone = intent.value, error = null)
            }

            RegisterIntent.Submit -> submit()
            RegisterIntent.OpenLogin -> sendEffect(RegisterEffect.NavigateToLogin)


        }
    }


    private fun submit() {
        viewModelScope.launch {
            val current = _state.value

            if(current.login.isBlank() || current.email.isBlank() || current.password.isBlank()){
                _state.value = current.copy(error = "Login, email and password are required")
                return@launch
            }
            if(current.password.length < 8){
                _state.value = current.copy(error = "Password must be at least 8 characters long")
                return@launch
            }

            _state.value = current.copy(isLoading = true, error = null)

            try{
                authRepository.register(
                    login = current.login.trim(),
                    email = current.email.trim(),
                    password = current.password,
                    phone = current.phone.trim().takeIf { it.isNotBlank() }
                )
                _state.value = _state.value.copy(isLoading = false)
                sendEffect(RegisterEffect.NavigateToProfile)
            } catch (e: ApiException) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Request failed"
                )
            } catch (e: Exception){
                Log.d("AuthErrorDebug", e.toString())
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Unexpected error"
                )
            }
        }
    }

    private fun sendEffect(effect: RegisterEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}

