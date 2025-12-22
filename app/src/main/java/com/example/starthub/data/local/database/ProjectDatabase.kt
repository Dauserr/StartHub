package com.example.starthub.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.starthub.data.local.dao.ProjectDao
import com.example.starthub.data.local.dao.ProfileDao
import com.example.starthub.data.local.model.ProjectEntity
import com.example.starthub.data.local.model.ProfileEntity

@Database(
    entities = [ProjectEntity::class, ProfileEntity::class],
    version = 2
)
abstract class ProjectDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun profileDao(): ProfileDao

    companion object {
        @Volatile
        private var INSTANCE: ProjectDatabase? = null

        fun getDatabase(context: Context): ProjectDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProjectDatabase::class.java,
                    "project_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
