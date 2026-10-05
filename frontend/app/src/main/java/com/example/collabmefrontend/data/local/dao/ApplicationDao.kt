package com.example.collabmefrontend.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.collabmefrontend.data.local.entity.ApplicationEntity

@Dao
interface ApplicationDao {
    @Query("SELECT * FROM applications")
    suspend fun getAll(): List<ApplicationEntity>

    @Upsert
    suspend fun upsertAll(entities: List<ApplicationEntity>)

    @Query("DELETE FROM applications")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(entities: List<ApplicationEntity>) {
        deleteAll()
        upsertAll(entities)
    }
}
