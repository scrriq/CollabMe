package com.example.features.users

import com.example.app.plugins.NotFoundException
import com.example.app.plugins.ValidationException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import java.util.UUID

fun Route.UsersRoutes(usersService: UsersService) {
    route("/users") {
        get("/{id}") {
            val idParam = call.parameters["id"]
                ?: throw ValidationException("Missing id parameter")

            val id = runCatching { UUID.fromString(idParam) }
                .getOrElse { throw ValidationException("Invalid UUID") }

            val user = usersService.getById(id)
                ?: throw NotFoundException("User not found")

            call.respond(user)
        }
    }
}