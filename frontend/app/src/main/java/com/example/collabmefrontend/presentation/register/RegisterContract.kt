package com.example.collabmefrontend.presentation.register

data class RegisterState(
    val login: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface RegisterIntent{
    data class LoginChanged(val value: String) : RegisterIntent
    data class EmailChanged(val value: String) : RegisterIntent
    data class PasswordChanged(val value: String) : RegisterIntent
    data class PhoneChanged(val value: String) : RegisterIntent
    data object Submit : RegisterIntent
    data object OpenLogin : RegisterIntent
}

sealed interface RegisterEffect{
    data object NavigateToProfile : RegisterEffect
    data object NavigateToLogin : RegisterEffect
}
