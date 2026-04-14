package com.example.features.users

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val login: String,
    val email: String,
    val phone: String? = null,
)

