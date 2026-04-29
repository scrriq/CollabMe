package com.example.collabmefrontend.data.remote.api

import android.util.Log
import com.example.collabmefrontend.core.network.ApiClient
import com.example.collabmefrontend.data.remote.dto.UserProfilePatchRequestDto
import com.example.collabmefrontend.data.remote.dto.UserProfilePutRequestDto
import com.example.collabmefrontend.data.remote.dto.UserProfileDto
import com.example.collabmefrontend.domain.model.UserProfile
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class ProfileNotFoundException(message: String) : RuntimeException(message)

class ProfileApi(
    private val client: HttpClient,
    private val baseUrl: String = ApiClient.BASE_URL
){
    suspend fun getMyProfile(token: String) : UserProfileDto {
        val response = client.get("$baseUrl/profile/me") {
            header(HttpHeaders.Authorization, "Bearer $token")
            accept(ContentType.Application.Json)
        }

        if (!response.status.isSuccess()) {
            throwProfileException(response.status.value, response.bodyAsText())
        }

        return response.body()
    }

    suspend fun getProfileByUserId(userId: String) : UserProfileDto{

        val response = client.get("$baseUrl/profile/$userId")
        if(!response.status.isSuccess()){
            if(response.status.value == 404){
                throw ProfileNotFoundException("User profile not found")
            }
            throw ApiException("Failder to load profile: HTTP ${response.status.value}")
        }

        return response.body()
    }

    suspend fun putMyProfile(token: String, request: UserProfilePutRequestDto): UserProfileDto {
        val response = client.put("$baseUrl/profile/me") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(request)
        }

        if (!response.status.isSuccess()) {
            throwProfileException(response.status.value, response.bodyAsText())
        }

        return response.body()
    }

    suspend fun patchMyProfile(token: String, request: UserProfilePatchRequestDto): UserProfileDto {
        val response = client.patch("$baseUrl/profile/me") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(request)
        }

        if (!response.status.isSuccess()) {
            throwProfileException(response.status.value, response.bodyAsText())
        }

        return response.body()
    }

    private fun throwProfileException(statusCode: Int, rawBody: String): Nothing {
        val message = parseErrorMessage(statusCode, rawBody)
        Log.e("ProfileApi", "HTTP $statusCode body=$rawBody")
        if (statusCode == 404) {
            throw ProfileNotFoundException(message)
        }
        throw ApiException(message)
    }

    private fun parseErrorMessage(statusCode: Int, rawBody: String): String {
        val parsedMessage = runCatching {
            val json = Json.parseToJsonElement(rawBody).jsonObject
            json["message"]?.jsonPrimitive?.content
                ?: json["error"]?.jsonPrimitive?.content
        }.getOrNull()

        val safeBody = rawBody.take(300).ifBlank { "empty response body" }
        return parsedMessage ?: "HTTP $statusCode: $safeBody"
    }
}