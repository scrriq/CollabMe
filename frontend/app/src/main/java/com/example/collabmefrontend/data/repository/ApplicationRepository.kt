package com.example.collabmefrontend.data.repository

import com.example.collabmefrontend.data.remote.dto.ApplicationCreateRequest
import com.example.collabmefrontend.data.remote.dto.ApplicationPatchRequest
import com.example.collabmefrontend.data.remote.dto.ApplicationResponseUserDto
import com.example.collabmefrontend.domain.model.ApplicationItem

interface ApplicationRepository {

    suspend fun getApplications(): List<ApplicationItem>

    suspend fun getMyResponseApplications(): List<ApplicationItem>

    suspend fun getApplicationById(
        applicationId: String
    ): ApplicationItem

    suspend fun hasResponded(applicationId: String): Boolean

    suspend fun respondToApplication(applicationId: String): ApplicationItem

    suspend fun withdrawResponse(applicationId: String)

    suspend fun listApplicationResponders(applicationId: String): List<ApplicationResponseUserDto>

    suspend fun createApplication(
        request: ApplicationCreateRequest
    ): ApplicationItem

    suspend fun patchApplication(
        applicationId: String,
        request: ApplicationPatchRequest
    ): ApplicationItem
}
