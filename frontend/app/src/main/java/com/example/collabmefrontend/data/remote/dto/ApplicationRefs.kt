package com.example.collabmefrontend.data.remote.dto

import com.example.collabmefrontend.domain.model.ApplicationKindRef
import com.example.collabmefrontend.domain.model.ApplicationStatusRef
import com.example.collabmefrontend.domain.model.ApplicationThemeRef
import kotlinx.serialization.Serializable

@Serializable
data class ApplicationThemeRefDto(
    val id: String,
    val name: String,
) {
    fun toDomain(): ApplicationThemeRef = ApplicationThemeRef(id = id, name = name)
}

@Serializable
data class ApplicationTitledRefDto(
    val id: String,
    val title: String,
) {
    fun toKindDomain(): ApplicationKindRef = ApplicationKindRef(id = id, title = title)
    fun toStatusDomain(): ApplicationStatusRef = ApplicationStatusRef(id = id, title = title)
}
