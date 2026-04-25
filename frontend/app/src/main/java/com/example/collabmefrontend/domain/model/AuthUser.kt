package com.example.collabmefrontend.domain.model

data class AuthUser(
    val id: String,
    val login: String,
    val email: String,
    val phone: String? = null
)
