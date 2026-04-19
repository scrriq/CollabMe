package com.example.features.auth

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val login: String,
    val email: String,
    val password: String,
    val phone: String? = null,
)

@Serializable
data class LoginRequest(
    val login: String,
    val password: String,
)

@Serializable
data class AuthUserDto(
    val id: String,
    val login: String,
    val email: String,
    val phone: String? = null,
)

@Serializable
data class AuthResponse(
    val accessToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
    val user: AuthUserDto,
)

@Serializable
data class MessageResponse(
    val message: String,
)
