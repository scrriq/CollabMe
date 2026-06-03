package com.example.features.applicationkinds

import java.util.UUID

class ApplicationKindsService(
    private val applicationKindsRepository: ApplicationKindsRepository
) {
    fun getAll() : List<ApplicationKindDto>{
        return applicationKindsRepository.findAll()
    }


    fun getById(id: UUID): ApplicationKindDto? {
        return applicationKindsRepository.findById(id)
    }
}
