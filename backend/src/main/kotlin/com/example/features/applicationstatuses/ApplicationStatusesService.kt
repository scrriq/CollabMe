package com.example.features.applicationstatuses

import java.util.UUID

class ApplicationStatusesService(
    private val applicationStatusesRepository: ApplicationStatusesRepository
) {
    fun getById(id: UUID): ApplicationStatusDto? {
        return applicationStatusesRepository.findById(id)
    }
}
