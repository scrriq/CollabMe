package com.example.collabmefrontend.data.repository

import com.example.collabmefrontend.data.remote.api.ApplicationApi
import com.example.collabmefrontend.data.remote.dto.ApplicationCreateRequest
import com.example.collabmefrontend.data.remote.dto.ApplicationPatchRequest
import com.example.collabmefrontend.domain.model.ApplicationItem

class RemoteApplicationRepository(
    private val api: ApplicationApi
) : ApplicationRepository {

    override suspend fun getApplications(): List<ApplicationItem> {
        return api.getApplications().map { it.toDomain() }
    }

    override suspend fun getMyResponseApplications(): List<ApplicationItem> {
        return api.getMyResponseApplications().map { it.toDomain() }
    }

    override suspend fun getApplicationById(
        applicationId: String
    ): ApplicationItem {
        return api.getApplicationById(applicationId).toDomain()
    }

    override suspend fun hasResponded(applicationId: String): Boolean {
        return api.getResponseStatus(applicationId).responded
    }

    override suspend fun respondToApplication(applicationId: String): ApplicationItem {
        return api.respondToApplication(applicationId).toDomain()
    }

    override suspend fun withdrawResponse(applicationId: String) {
        api.withdrawResponse(applicationId)
    }

    override suspend fun createApplication(
        request: ApplicationCreateRequest
    ): ApplicationItem {
        return api.createApplication(request).toDomain()
    }

    override suspend fun patchApplication(
        applicationId: String,
        request: ApplicationPatchRequest
    ): ApplicationItem {
        return api.patchApplication(
            applicationId,
            request
        ).toDomain()
    }
}
