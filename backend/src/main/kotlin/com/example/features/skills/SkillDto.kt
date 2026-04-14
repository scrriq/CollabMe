package com.example.features.skills

import kotlinx.serialization.Serializable

@Serializable
data class SkillDto(
    val id: String,
    val name: String,
)
