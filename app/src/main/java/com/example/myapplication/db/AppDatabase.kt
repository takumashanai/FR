package com.example.myapplication.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.dao.GitHubDao
import com.example.myapplication.dao.GitHubRepositoryDao
import com.example.myapplication.data.GitHubRepositoryUser
import com.example.myapplication.data.GitHubUser

@Database(entities = [GitHubUser::class,GitHubRepositoryUser::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase(){

    abstract fun gitHubDao(): GitHubDao
    abstract fun gitHubRepositoryDao(): GitHubRepositoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, "app_database"
            )
                .fallbackToDestructiveMigration()
                .build()
    }
}