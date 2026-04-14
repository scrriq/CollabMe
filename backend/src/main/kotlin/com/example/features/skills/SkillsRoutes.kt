package com.example.features.skills

import com.example.app.plugins.NotFoundException
import com.example.app.plugins.ValidationException
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import java.util.UUID

fun Route.SkillsRoutes(skillsService: SkillsService) {
    route("/skills") {
        get("/{id}") {
            val idParam = call.parameters["id"]
                ?: throw ValidationException("Missing id parameter")

            val id = runCatching { UUID.fromString(idParam) }
                .getOrElse { throw ValidationException("Invalid UUID") }

            val skill = skillsService.getById(id)
                ?: throw NotFoundException("Skill not found")

            call.respond(skill)
        }
    }
}
