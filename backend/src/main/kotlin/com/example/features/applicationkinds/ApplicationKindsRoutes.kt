package com.example.features.applicationkinds

import com.example.app.plugins.NotFoundException
import com.example.app.plugins.ValidationException
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import java.util.UUID

fun Route.ApplicationKindsRoutes(applicationKindsService: ApplicationKindsService) {
    route("/application-kinds") {
        get{
            call.respond(applicationKindsService.getAll())
        }

        get("/{id}") {
            val idParam = call.parameters["id"]
                ?: throw ValidationException("Missing id parameter")

            val id = runCatching { UUID.fromString(idParam) }
                .getOrElse { throw ValidationException("Invalid UUID") }

            val applicationKind = applicationKindsService.getById(id)
                ?: throw NotFoundException("Application kind not found")

            call.respond(applicationKind)
        }
    }
}
