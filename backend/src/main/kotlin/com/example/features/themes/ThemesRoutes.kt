package com.example.features.themes

import com.example.app.plugins.NotFoundException
import com.example.app.plugins.ValidationException
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import java.util.UUID

fun Route.ThemesRoutes(themesService: ThemesService) {
    route("/themes") {
        get("/{id}") {
            val idParam = call.parameters["id"]
                ?: throw ValidationException("Missing id parameter")

            val id = runCatching { UUID.fromString(idParam) }
                .getOrElse { throw ValidationException("Invalid UUID") }

            val theme = themesService.getById(id)
                ?: throw NotFoundException("Theme not found")

            call.respond(theme)
        }
    }
}
