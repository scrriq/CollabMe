package com.example.features.directions

import java.util.UUID

class DirectionsService(
    private val directionsRepository: DirectionsRepository
) {
    fun getAll(): List<DirectionDto>{
        return directionsRepository.findAll()
    }
    fun getById(id: UUID): DirectionDto? {
        return directionsRepository.findById(id)
    }
}
