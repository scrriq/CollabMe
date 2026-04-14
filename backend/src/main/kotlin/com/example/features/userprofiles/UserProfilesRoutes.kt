package com.example.features.userprofiles

import com.example.app.plugins.NotFoundException
import com.example.app.plugins.ValidationException
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import java.util.UUID

fun Route.UserProfilesRoutes(userProfilesService: UserProfilesService) {
    route("/user-profiles") {
        get("/{userId}") {
            val userIdParam = call.parameters["userId"]
                ?: throw ValidationException("Missing userId parameter")

            val userId = runCatching { UUID.fromString(userIdParam) }
                .getOrElse { throw ValidationException("Invalid UUID") }

            val profile = userProfilesService.getByUserId(userId)
                ?: throw NotFoundException("User profile not found")

            call.respond(profile)
        }
    }
}
