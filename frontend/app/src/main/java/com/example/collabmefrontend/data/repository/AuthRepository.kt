package com.example.collabmefrontend.data.repository

import com.example.collabmefrontend.domain.model.AuthUser

interface AuthRepository{
    suspend fun register(
        login: String,
        email: String,
        password: String,
        phone: String?
    ) : AuthUser

    suspend fun login(
        login: String,
        password: String,
    ) : AuthUser

    suspend fun logout()
}