package com.example.collabmefrontend.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.collabmefrontend.data.local.entity.FavoriteApplicationEntity

@Dao
interface FavoriteApplicationDao {
    @Query("SELECT * FROM favorite_applications")
    suspend fun getAll(): List<FavoriteApplicationEntity>

    @Upsert
    suspend fun upsertAll(entities: List<FavoriteApplicationEntity>)

    @Query("DELETE FROM favorite_applications")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(entities: List<FavoriteApplicationEntity>) {
        deleteAll()
        upsertAll(entities)
    }
}
