package com.example.app

import com.example.app.plugins.configureRouting
import com.example.app.plugins.configureSerialization
import com.example.app.plugins.configureStatusPages
import com.example.db.DatabaseFactory
import com.example.features.users.UsersRepository
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.util.UUID

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {

    configureSerialization()
    configureStatusPages()

    DatabaseFactory.init(this)

    configureRouting()
}

