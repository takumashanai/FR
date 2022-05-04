package com.example.myapplication.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.data.GitHubRepositoryUser
import kotlinx.coroutines.flow.Flow

@Dao
interface GitHubRepositoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: GitHubRepositoryUser)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(userList: List<GitHubRepositoryUser>)

    @Query("DELETE FROM github_repository_user_table")
    suspend fun clearAll(): Int

    @Query("SELECT * FROM github_repository_user_table")
    fun getFlow(): Flow<List<GitHubRepositoryUser>>
}