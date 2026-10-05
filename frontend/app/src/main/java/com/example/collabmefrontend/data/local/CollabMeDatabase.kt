package com.example.collabmefrontend.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.collabmefrontend.data.local.dao.ApplicationDao
import com.example.collabmefrontend.data.local.dao.FavoriteApplicationDao
import com.example.collabmefrontend.data.local.dao.UserProfileDao
import com.example.collabmefrontend.data.local.entity.ApplicationEntity
import com.example.collabmefrontend.data.local.entity.FavoriteApplicationEntity
import com.example.collabmefrontend.data.local.entity.UserProfileEntity

@Database(
    entities = [
        UserProfileEntity::class,
        ApplicationEntity::class,
        FavoriteApplicationEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class CollabMeDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun applicationDao(): ApplicationDao
    abstract fun favoriteApplicationDao(): FavoriteApplicationDao

    companion object {
        @Volatile
        private var instance: CollabMeDatabase? = null

        fun getInstance(context: Context): CollabMeDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    CollabMeDatabase::class.java,
                    "collabme.db",
                ).build().also { instance = it }
            }
        }
    }
}
