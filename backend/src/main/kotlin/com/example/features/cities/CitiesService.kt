package com.example.features.cities

import java.util.UUID

class CitiesService(
    private val citiesRepository: CitiesRepository
) {
    fun getAll(): List<CityDto> {
        return citiesRepository.findAll()
    }

    fun getById(id: UUID): CityDto? {
        return citiesRepository.findById(id)
    }
}
