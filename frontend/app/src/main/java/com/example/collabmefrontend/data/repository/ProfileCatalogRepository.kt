package com.example.collabmefrontend.data.repository

import com.example.collabmefrontend.data.remote.api.CatalogApi

data class CatalogOption(
    val id: String,
    val label: String,
)

interface ProfileCatalogRepository {
    suspend fun getCities(): List<CatalogOption>
    suspend fun getUniversities(): List<CatalogOption>

    suspend fun getDirections(): List<CatalogOption>
}

class RemoteProfileCatalogRepository(
    private val catalogApi: CatalogApi
) : ProfileCatalogRepository {
    override suspend fun getCities(): List<CatalogOption> {
        return catalogApi.getCities().map { CatalogOption(id = it.id, label = it.name) }
    }

    override suspend fun getUniversities(): List<CatalogOption> {
        return catalogApi.getUniversities().map { CatalogOption(id = it.id, label = it.name) }
    }

    override suspend fun getDirections(): List<CatalogOption> {
        return catalogApi.getDirections().map { CatalogOption(id = it.id, label = it.name) }
    }
}
