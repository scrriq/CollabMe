package com.example.features.applicationresponses

import com.example.app.plugins.ValidationException
import com.example.features.auth.requireJwtUserId
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import java.util.UUID

fun Route.ApplicationResponsesRoutes(applicationResponsesService: ApplicationResponsesService) {
    authenticate("auth-jwt") {
        route("/applications") {
            get("/responses/me") {
                val userId = call.requireJwtUserId()
                val applications = applicationResponsesService.listMyResponseApplications(userId)
                call.respond(applications)
            }

            post("/{id}/responses") {
                val userId = call.requireJwtUserId()
                val applicationId = call.parseApplicationId()
                val application = applicationResponsesService.respond(userId, applicationId)
                call.respond(HttpStatusCode.Created, application)
            }

            delete("/{id}/responses") {
                val userId = call.requireJwtUserId()
                val applicationId = call.parseApplicationId()
                applicationResponsesService.withdraw(userId, applicationId)
                call.respond(HttpStatusCode.NoContent)
            }

            get("/{id}/responses") {
                val userId = call.requireJwtUserId()
                val applicationId = call.parseApplicationId()
                val responders = applicationResponsesService.listResponders(applicationId, userId)
                call.respond(responders)
            }

            get("/{id}/responses/status") {
                val userId = call.requireJwtUserId()
                val applicationId = call.parseApplicationId()
                val responded = applicationResponsesService.hasResponded(userId, applicationId)
                call.respond(ApplicationResponseStatusDto(responded = responded))
            }
        }
    }
}

private fun ApplicationCall.parseApplicationId(): UUID {
    val idParam = parameters["id"] ?: throw ValidationException("Missing id parameter")
    return runCatching { UUID.fromString(idParam) }
        .getOrElse { throw ValidationException("Invalid UUID") }
}
