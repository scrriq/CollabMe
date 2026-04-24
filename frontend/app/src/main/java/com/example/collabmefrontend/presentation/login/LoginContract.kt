package com.example.collabmefrontend.presentation.login

import android.util.Log

data class LoginState (
    val login: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
sealed interface LoginIntent{
    data class LoginChanged(val value: String) : LoginIntent
    data class PasswordChanged(val value: String) : LoginIntent
    data object Submit : LoginIntent
    data object OpenRegister : LoginIntent
}


sealed interface LoginEffect{
    data object NavigateToProfile: LoginEffect
    data object NavigateToRegister: LoginEffect
}

