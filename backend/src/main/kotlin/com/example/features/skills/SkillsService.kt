package com.example.features.skills

import java.util.UUID

class SkillsService(
    private val skillsRepository: SkillsRepository
) {
    fun getById(id: UUID): SkillDto? {
        return skillsRepository.findById(id)
    }
}
