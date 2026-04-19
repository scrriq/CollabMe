package com.example.features.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.app.plugins.JwtConfig
import java.util.Date

class JwtTokenService(
    private val jwtConfig: JwtConfig
) {
    fun createAccessToken(user: AuthUserDto): String {
        val expiresAt = Date(System.currentTimeMillis() + jwtConfig.accessTokenTtlSeconds * 1000)

        return JWT.create()
            .withIssuer(jwtConfig.issuer)
            .withAudience(jwtConfig.audience)
            .withClaim("userId", user.id)
            .withClaim("login", user.login)
            .withExpiresAt(expiresAt)
            .sign(Algorithm.HMAC256(jwtConfig.secret))
    }
}
