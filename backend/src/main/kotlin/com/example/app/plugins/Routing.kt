package com.example.app.plugins

import com.example.features.users.UsersRepository
import com.example.features.users.UsersRoutes
import com.example.features.users.UsersService
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    val usersRepository = UsersRepository()
    val usersService = UsersService(usersRepository)

    routing {
        UsersRoutes(usersService)
    }
}