package com.example.features.userprofiles

import com.example.app.plugins.NotFoundException
import com.example.app.plugins.ValidationException
import com.example.features.auth.requireJwtUserId
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import java.util.UUID

fun Route.UserProfilesRoutes(userProfilesService: UserProfilesService) {
    route("/profile") {
        authenticate("auth-jwt") {
            get("/me") {
                val userId = call.requireJwtUserId()
                val profile = userProfilesService.getByUserId(userId)
                    ?: throw NotFoundException("User profile not found")
                call.respond(profile)
            }
            put("/me") {
                val userId = call.requireJwtUserId()
                val body = call.receive<UserProfilePutRequest>()
                val profile = userProfilesService.put(userId, body)
                call.respond(profile)
            }
            put {
                val userId = call.requireJwtUserId()
                val body = call.receive<UserProfilePutRequest>()
                val profile = userProfilesService.put(userId, body)
                call.respond(profile)
            }
            patch("/me") {
                val userId = call.requireJwtUserId()
                val body = call.receive<UserProfilePatchRequest>()
                val profile = userProfilesService.patch(userId, body)
                call.respond(profile)
            }
            patch {
                val userId = call.requireJwtUserId()
                val body = call.receive<UserProfilePatchRequest>()
                val profile = userProfilesService.patch(userId, body)
                call.respond(profile)
            }
        }
        get("/{userId}") {
            val userIdParam = call.parameters["userId"]
                ?: throw ValidationException("Missing userId parameter")

            if (userIdParam == "me") {
                throw ValidationException("Use GET /profile/me with authentication")
            }

            val userId = runCatching { UUID.fromString(userIdParam) }
                .getOrElse { throw ValidationException("Invalid UUID") }

            val profile = userProfilesService.getByUserId(userId)
                ?: throw NotFoundException("User profile not found")

            call.respond(profile)
        }
    }
}
