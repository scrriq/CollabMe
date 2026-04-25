package com.example.collabmefrontend.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDto(
    val login: String,
    val email: String,
    val password: String,
    val phone: String? = null
)

@Serializable
data class LoginRequestDto(
    val login: String,
    val password: String
)

@Serializable
data class AuthUserDto(
    val id: String,
    val login: String,
    val email: String,
    val phone: String? = null
)

@Serializable
data class AuthResponseDto(
    val accessToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
    val user: AuthUserDto
)