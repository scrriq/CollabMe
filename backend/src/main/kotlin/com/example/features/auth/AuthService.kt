package com.example.features.auth

import com.example.app.plugins.UnauthorizedException
import com.example.app.plugins.ValidationException

class AuthService(
    private val authRepository: AuthRepository,
    private val jwtTokenService: JwtTokenService,
    private val accessTokenTtlSeconds: Long,
) {
    fun register(request: RegisterRequest): AuthResponse {
        validateRegisterRequest(request)

        if (authRepository.existsByLoginOrEmail(request.login, request.email)) {
            throw ValidationException("User with such login or email already exists")
        }

        val user = authRepository.createUser(
            login = request.login.trim(),
            email = request.email.trim(),
            passwordHash = PasswordHasher.hash(request.password),
            phone = request.phone?.trim()?.takeIf { it.isNotBlank() },
        )

        return AuthResponse(
            accessToken = jwtTokenService.createAccessToken(user),
            expiresIn = accessTokenTtlSeconds,
            user = user,
        )
    }

    fun login(request: LoginRequest): AuthResponse {
        if (request.login.isBlank() || request.password.isBlank()) {
            throw ValidationException("Login and password are required")
        }

        val credentials = authRepository.findByLogin(request.login.trim())
            ?: throw UnauthorizedException("Invalid credentials")

        val isPasswordValid = PasswordHasher.verify(request.password, credentials.passwordHash)
        if (!isPasswordValid) {
            throw UnauthorizedException("Invalid credentials")
        }

        return AuthResponse(
            accessToken = jwtTokenService.createAccessToken(credentials.user),
            expiresIn = accessTokenTtlSeconds,
            user = credentials.user,
        )
    }

    fun logout(): MessageResponse {
        // JWT access tokens are stateless; for now logout is client-side token disposal.
        return MessageResponse("Logged out successfully")
    }

    private fun validateRegisterRequest(request: RegisterRequest) {
        if (request.login.isBlank() || request.email.isBlank() || request.password.isBlank()) {
            throw ValidationException("Login, email and password are required")
        }
        if (request.password.length < 8) {
            throw ValidationException("Password must be at least 8 characters long")
        }
    }
}
