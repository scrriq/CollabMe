package com.example.features.themes

import java.util.UUID

class ThemesService(
    private val themesRepository: ThemesRepository
) {
    fun getAll(): List<ThemeDto> = themesRepository.findAll()
    fun getById(id: UUID): ThemeDto? {
        return themesRepository.findById(id)
    }
}
