package com.example.collabmefrontend.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object ApiClient {
    const val BASE_URL = "http://10.0.2.2:8080"

    fun create() : HttpClient = HttpClient(Android){
        install(ContentNegotiation){
            json(
                Json{
                    ignoreUnknownKeys = true
                    explicitNulls = false
                }
            )
        }
        install(Logging){
            level = LogLevel.BODY
        }
    }
}