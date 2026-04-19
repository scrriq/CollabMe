package com.example.app

import com.example.app.plugins.configureRouting
import com.example.app.plugins.configureSecurity
import com.example.app.plugins.configureSerialization
import com.example.app.plugins.configureStatusPages
import com.example.db.DatabaseFactory
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {

    configureSerialization()
    configureStatusPages()
    configureSecurity()

    DatabaseFactory.init(this)

    configureRouting()
}

