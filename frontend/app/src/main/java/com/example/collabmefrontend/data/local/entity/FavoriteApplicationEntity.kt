package com.example.collabmefrontend.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_applications")
data class FavoriteApplicationEntity(
    @PrimaryKey val id: String,
    val payload: String,
    val cachedAt: Long,
)
