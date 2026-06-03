package com.example.collabmefrontend.data.repository

import com.example.collabmefrontend.data.remote.api.CatalogApi

interface ApplicationCatalogRepository {
    suspend fun getThemes(): List<CatalogOption>
    suspend fun getKinds(): List<CatalogOption>
    suspend fun getStatuses(): List<CatalogOption>
}

class RemoteApplicationCatalogRepository(
    private val catalogApi: CatalogApi,
) : ApplicationCatalogRepository {
    override suspend fun getThemes(): List<CatalogOption> {
        return catalogApi.getThemes().map { CatalogOption(id = it.id, label = it.name) }
    }

    override suspend fun getKinds(): List<CatalogOption> {
        return catalogApi.getApplicationKinds().map { CatalogOption(id = it.id, label = it.title) }
    }

    override suspend fun getStatuses(): List<CatalogOption> {
        return catalogApi.getApplicationStatuses().map { CatalogOption(id = it.id, label = it.title) }
    }
}
