package com.example.features.cities

import java.util.UUID

class CitiesService(
    private val citiesRepository: CitiesRepository
) {
    fun getById(id: UUID): CityDto? {
        return citiesRepository.findById(id)
    }
}
