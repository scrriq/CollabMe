package com.example.app.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond

class NotFoundException(message: String) : RuntimeException(message)
class ValidationException(message: String) : RuntimeException(message)
class UnauthorizedException(message: String) : RuntimeException(message)
class ForbiddenException(message: String) : RuntimeException(message)

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<ValidationException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                mapOf(
                    "error" to "VALIDATION_ERROR",
                    "message" to (cause.message ?: "Validation error")
                )
            )
        }

        exception<NotFoundException> { call, cause ->
            call.respond(
                HttpStatusCode.NotFound,
                mapOf(
                    "error" to "NOT_FOUND",
                    "message" to (cause.message ?: "Not found")
                )
            )
        }

        exception<UnauthorizedException> { call, cause ->
            call.respond(
                HttpStatusCode.Unauthorized,
                mapOf(
                    "error" to "UNAUTHORIZED",
                    "message" to (cause.message ?: "Unauthorized")
                )
            )
        }

        exception<ForbiddenException> { call, cause ->
            call.respond(
                HttpStatusCode.Forbidden,
                mapOf(
                    "error" to "FORBIDDEN",
                    "message" to (cause.message ?: "Forbidden")
                )
            )
        }

        exception<Throwable> { call, _ ->
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("error" to "INTERNAL_SERVER_ERROR")
            )
        }
    }
}