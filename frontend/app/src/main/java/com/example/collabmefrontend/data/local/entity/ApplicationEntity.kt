package com.example.collabmefrontend.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "applications")
data class ApplicationEntity(
    @PrimaryKey val id: String,
    val payload: String,
    val cachedAt: Long,
)
