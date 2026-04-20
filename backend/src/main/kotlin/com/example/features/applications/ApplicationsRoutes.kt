package com.example.features.applications

import com.example.app.plugins.ValidationException
import com.example.features.auth.requireJwtUserId
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.application.ApplicationCall
import java.util.UUID

fun Route.ApplicationsRoutes(applicationsService: ApplicationsService) {
    route("/applications") {
        get {
            val excludeCompleted = call.request.queryParameters["excludeCompleted"]?.equals("true", ignoreCase = true) == true
            val userIdFilter = call.request.queryParameters["userId"]?.let { parseUserId(it) }
            val list = applicationsService.listVisible(userIdFilter, excludeCompleted)
            call.respond(list)
        }
        get("/{id}") {
            val id = call.parseApplicationId()
            val application = applicationsService.getVisibleById(id)
            call.respond(application)
        }
        authenticate("auth-jwt") {
            post {
                val userId = call.requireJwtUserId()
                val body = call.receive<ApplicationCreateRequest>()
                val created = applicationsService.create(userId, body)
                call.respond(HttpStatusCode.Created, created)
            }
            patch("/{id}") {
                val userId = call.requireJwtUserId()
                val id = call.parseApplicationId()
                val body = call.receive<ApplicationPatchRequest>()
                val application = applicationsService.patch(id, userId, body)
                call.respond(application)
            }
            delete("/{id}") {
                val userId = call.requireJwtUserId()
                val id = call.parseApplicationId()
                applicationsService.softDelete(id, userId)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}

private fun ApplicationCall.parseApplicationId(): UUID {
    val idParam = parameters["id"] ?: throw ValidationException("Missing id parameter")
    return runCatching { UUID.fromString(idParam) }
        .getOrElse { throw ValidationException("Invalid UUID") }
}

private fun parseUserId(raw: String): UUID =
    runCatching { UUID.fromString(raw) }
        .getOrElse { throw ValidationException("Invalid userId UUID") }
