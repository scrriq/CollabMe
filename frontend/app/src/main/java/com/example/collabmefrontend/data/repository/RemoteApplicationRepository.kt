package com.example.collabmefrontend.data.repository

import com.example.collabmefrontend.data.remote.api.ApplicationApi
import com.example.collabmefrontend.domain.model.ApplicationItem

class RemoteApplicationRepository(
    private val api: ApplicationApi
) : ApplicationRepository {

    override suspend fun getApplications(): List<ApplicationItem> {
        return api.getApplications().map { it.toDomain() }
    }

    override suspend fun getApplicationById(applicationId: String): ApplicationItem {
        return api.getApplicationsById(applicationId).toDomain()
    }
}