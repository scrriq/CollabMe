package com.example.features.applicationstatuses

import java.util.UUID

class ApplicationStatusesService(
    private val applicationStatusesRepository: ApplicationStatusesRepository
) {
    fun getAll(): List<ApplicationStatusDto>{
        return applicationStatusesRepository.findAll()
    }

    fun getById(id: UUID): ApplicationStatusDto? {
        return applicationStatusesRepository.findById(id)
    }
}
