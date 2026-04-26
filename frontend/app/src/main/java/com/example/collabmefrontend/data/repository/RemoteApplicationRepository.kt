package com.example.collabmefrontend.data.repository

import com.example.collabmefrontend.data.remote.api.ApplicationApi
import com.example.collabmefrontend.domain.model.ApplicationItem

class RemoteApplicationRepository(
    private val api: ApplicationApi
) : ApplicationRepository {

    override suspend fun getApplications(): List<ApplicationItem> {
        return api.getApplications().map { it.toDomain() }
    }

}