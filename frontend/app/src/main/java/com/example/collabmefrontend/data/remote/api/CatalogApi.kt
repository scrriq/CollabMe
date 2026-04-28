package com.example.collabmefrontend.data.remote.api

import android.util.Log
import com.example.collabmefrontend.core.network.ApiClient
import com.example.collabmefrontend.data.remote.dto.CityDto
import com.example.collabmefrontend.data.remote.dto.UniversityDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.isSuccess

class CatalogApi(
    private val client: HttpClient,
    private val baseUrl: String = ApiClient.BASE_URL
) {
    suspend fun getCities(): List<CityDto> {
        val response = client.get("$baseUrl/cities") {
            accept(ContentType.Application.Json)
        }
        if (!response.status.isSuccess()) {
            throw ApiException("Failed to load cities: HTTP ${response.status.value}")
        }

        val result: List<CityDto> = response.body()

        Log.d("CitiDebug", result.toString())
        return response.body()
    }

    suspend fun getUniversities(): List<UniversityDto> {
        val response = client.get("$baseUrl/universities") {
            accept(ContentType.Application.Json)
        }
        if (!response.status.isSuccess()) {
            throw ApiException("Failed to load universities: HTTP ${response.status.value}")
        }
        return response.body()
    }
}
