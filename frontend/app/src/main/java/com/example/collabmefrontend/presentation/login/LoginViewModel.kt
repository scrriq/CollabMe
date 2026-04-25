package com.example.collabmefrontend.presentation.login

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


// в дальнейшем в LoginViewModel параметром необходимо будет передавать репощиторий
class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _effect = Channel<LoginEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()


    fun onIntent(intent: LoginIntent){
        when(intent){
            is LoginIntent.LoginChanged -> {
                _state.value = _state.value.copy(login = intent.value, error = null)
            }
            is LoginIntent.PasswordChanged -> {
                _state.value = _state.value.copy(password = intent.value, error = null)
            }

            LoginIntent.Submit -> submit()
            LoginIntent.OpenRegister -> sendEffect(LoginEffect.NavigateToRegister)

        }
    }

    private fun submit() {
        viewModelScope.launch {
            val current = _state.value

            if(current.login.isBlank() || current.password.isBlank()){
                _state.value = current.copy(error = "Login and password are required")
                return@launch
            }

            _state.value = current.copy(isLoading = true, error = null)

            // в дальнейшем необходимо реализовать обработчик e: AuthException с выводом ошибки
            try{
                authRepository.login(
                    login = current.login.trim(),
                    password = current.password
                )
                _state.value = _state.value.copy(isLoading = false)
                sendEffect(LoginEffect.NavigateToProfile)
            } catch (e: ApiException) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Request failed"
                )
            } catch (e: Exception){
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Unexpected error"
                )
            }

        }
    }

    private fun sendEffect(effect: LoginEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}