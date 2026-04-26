package com.example.collabmefrontend.data.repository

import com.example.collabmefrontend.domain.model.ApplicationItem

interface ApplicationRepository {
    suspend fun getApplications() : List<ApplicationItem>
}