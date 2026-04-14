package com.example.features.applicationkinds

import java.util.UUID

class ApplicationKindsService(
    private val applicationKindsRepository: ApplicationKindsRepository
) {
    fun getById(id: UUID): ApplicationKindDto? {
        return applicationKindsRepository.findById(id)
    }
}
