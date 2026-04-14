package com.example.features.universities

import java.util.UUID

class UniversitiesService(
    private val universitiesRepository: UniversitiesRepository
) {
    fun getById(id: UUID): UniversityDto? {
        return universitiesRepository.findById(id)
    }
}
