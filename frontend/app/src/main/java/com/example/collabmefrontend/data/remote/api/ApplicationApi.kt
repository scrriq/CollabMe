package com.example.collabmefrontend.data.remote.api

import com.example.collabmefrontend.data.remote.dto.ApplicationDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class ApplicationApi(
    private val client: HttpClient,
    private val baseUrl: String
) {
    suspend fun getApplications() : List<ApplicationDto>{
        return client.get("$baseUrl/applications").body()
    }

    suspend fun getApplicationsById(applicationId: String) : ApplicationDto{
        return client.get("$baseUrl/applications/$applicationId").body()
    }
}