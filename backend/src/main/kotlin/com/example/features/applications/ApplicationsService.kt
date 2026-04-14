package com.example.features.applications

import java.util.UUID

class ApplicationsService(
    private val applicationsRepository: ApplicationsRepository
) {
    fun getById(id: UUID): ApplicationDto? {
        return applicationsRepository.findById(id)
    }
}
