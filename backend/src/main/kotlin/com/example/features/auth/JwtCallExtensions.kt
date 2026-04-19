package com.example.features.auth

import com.example.app.plugins.UnauthorizedException
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import java.util.UUID

// Чтобы в route каждый раз не писать проверку на авторизацию, мы выделяем функцию
// Затем уже в роуте вызываем ее val userId = call.requireJwtUserId()

fun ApplicationCall.requireJwtUserId(): UUID {
    val principal = principal<JWTPrincipal>() ?: throw UnauthorizedException("Unauthorized")
    val userIdStr = principal.payload.getClaim("userId").asString()
        ?: throw UnauthorizedException("Unauthorized")
    return runCatching { UUID.fromString(userIdStr) }
        .getOrElse { throw UnauthorizedException("Invalid token") }
}
