package com.example.collabmefrontend.data.repository

import com.example.collabmefrontend.data.remote.api.AuthApi
import com.example.collabmefrontend.data.remote.dto.LoginRequestDto
import com.example.collabmefrontend.data.remote.dto.RegisterRequestDto
import com.example.collabmefrontend.data.storage.TokenStorage
import com.example.collabmefrontend.domain.model.AuthUser

class RemoteAuthRepository(
    private val api: AuthApi,
    private val tokenStorage: TokenStorage
) : AuthRepository {

    // реализация функции регситрации
    override suspend fun register(
        login: String,
        email: String,
        password: String,
        phone: String?
    ): AuthUser {
        val response = api.register(
            RegisterRequestDto(
                login = login,
                email = email,
                password = password,
                phone = phone
            )
        )

        tokenStorage.saveToken(response.accessToken)

        return AuthUser(
            id = response.user.id,
            login = response.user.login,
            email = response.user.email,
            phone = response.user.phone
        )
    }

    // реализация функици login
    override suspend fun login(
        login: String,
        password: String
    ): AuthUser {
        val response = api.login(
            LoginRequestDto(
                login = login,
                password = password
            )
        )
        tokenStorage.saveToken(response.accessToken)

        return AuthUser(
            id = response.user.id,
            login = response.user.login,
            email = response.user.email,
            phone = response.user.phone
        )
    }

    override suspend fun logout() {
        tokenStorage.clear()
    }
}