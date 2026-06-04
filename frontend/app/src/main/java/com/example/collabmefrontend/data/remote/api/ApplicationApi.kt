package com.example.collabmefrontend.data.remote.api

import com.example.collabmefrontend.data.remote.dto.ApplicationCreateRequest
import com.example.collabmefrontend.data.remote.dto.ApplicationDto
import com.example.collabmefrontend.data.remote.dto.ApplicationPatchRequest
import com.example.collabmefrontend.data.remote.dto.ApplicationResponseStatusDto
import com.example.collabmefrontend.data.remote.dto.ApplicationResponseUserDto
import com.example.collabmefrontend.data.storage.TokenStorage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class ApplicationApi(
    private val client: HttpClient,
    private val baseUrl: String,
    private val tokenStorage: TokenStorage
) {
    suspend fun getApplications(): List<ApplicationDto> {
        return client.get("$baseUrl/applications").body()
    }

    suspend fun getApplicationById(applicationId: String): ApplicationDto {
        return client.get("$baseUrl/applications/$applicationId").body()
    }

    suspend fun createApplication(request: ApplicationCreateRequest): ApplicationDto {
        return client.post("$baseUrl/applications") {
            applyAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun patchApplication(
        applicationId: String,
        request: ApplicationPatchRequest
    ): ApplicationDto {
        return client.patch("$baseUrl/applications/$applicationId") {
            applyAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getMyResponseApplications(): List<ApplicationDto> {
        return client.get("$baseUrl/applications/responses/me") {
            applyAuthHeader()
        }.body()
    }

    suspend fun getResponseStatus(applicationId: String): ApplicationResponseStatusDto {
        return client.get("$baseUrl/applications/$applicationId/responses/status") {
            applyAuthHeader()
        }.body()
    }

    suspend fun respondToApplication(applicationId: String): ApplicationDto {
        return client.post("$baseUrl/applications/$applicationId/responses") {
            applyAuthHeader()
        }.body()
    }

    suspend fun withdrawResponse(applicationId: String) {
        client.delete("$baseUrl/applications/$applicationId/responses") {
            applyAuthHeader()
        }
    }

    suspend fun getApplicationResponders(applicationId: String): List<ApplicationResponseUserDto> {
        return client.get("$baseUrl/applications/$applicationId/responses") {
            applyAuthHeader()
        }.body()
    }

    private suspend fun io.ktor.client.request.HttpRequestBuilder.applyAuthHeader() {
        val token = tokenStorage.getToken() // если у тебя метод называется иначе, замени здесь
        if (!token.isNullOrBlank()) {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
    }
}