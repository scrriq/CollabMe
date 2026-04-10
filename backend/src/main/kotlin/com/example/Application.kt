package com.example

import com.example.db.DatabaseFactory
import com.example.users.UsersRepository
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.util.UUID

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    DatabaseFactory.init(this)
    val usersRepo = UsersRepository()

    routing {
        get("/users/{id}") {
            val idParam = call.parameters["id"] // извлекаем параметр id
            val id = runCatching { UUID.fromString(idParam) }.getOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid UUID")) // проверка на UUID

            val user = usersRepo.findById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found")) // Поиск пользователя

            call.respond(user)
        }
    }
}

