package com.example.collabmefrontend.data.remote.api

import com.example.collabmefrontend.core.network.ApiClient
import com.example.collabmefrontend.data.remote.dto.AuthResponseDto
import com.example.collabmefrontend.data.remote.dto.LoginRequestDto
import com.example.collabmefrontend.data.remote.dto.RegisterRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class ApiException(message: String) : RuntimeException(message)

class AuthApi(
    private val client: HttpClient,
    private val baseUrl: String = ApiClient.BASE_URL
) {
    suspend fun register(request: RegisterRequestDto): AuthResponseDto {
        val response = client.post("$baseUrl/auth/register") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(request)
        }

        if (!response.status.isSuccess()) {
            throw ApiException(parseErrorMessage(response.bodyAsText()))
        }

        return response.body()
    }

    suspend fun login(request: LoginRequestDto): AuthResponseDto {
        val response = client.post("$baseUrl/auth/login") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(request)
        }

        if (!response.status.isSuccess()) {
            throw ApiException(parseErrorMessage(response.bodyAsText()))
        }

        return response.body()
    }

    private fun parseErrorMessage(rawBody: String): String {
        return runCatching {
            Json.parseToJsonElement(rawBody)
                .jsonObject["message"]
                ?.jsonPrimitive
                ?.content
        }.getOrNull()
            ?: "Request failed"
    }
}