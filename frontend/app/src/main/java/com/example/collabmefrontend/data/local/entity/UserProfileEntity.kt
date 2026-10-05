package com.example.collabmefrontend.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val userId: String,
    val payload: String,
    val isMine: Boolean,
    val cachedAt: Long,
)
