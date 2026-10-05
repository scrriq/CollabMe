package com.example.collabmefrontend.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.collabmefrontend.data.local.entity.UserProfileEntity

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles WHERE isMine = 1 LIMIT 1")
    suspend fun getMyProfile(): UserProfileEntity?

    @Query("SELECT * FROM user_profiles WHERE userId = :userId LIMIT 1")
    suspend fun getByUserId(userId: String): UserProfileEntity?

    @Upsert
    suspend fun upsert(entity: UserProfileEntity)

    @Upsert
    suspend fun upsertAll(entities: List<UserProfileEntity>)
}
