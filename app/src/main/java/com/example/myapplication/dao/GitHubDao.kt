package com.example.myapplication.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.example.myapplication.data.GitHubUser

@Dao
interface GitHubDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: GitHubUser)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(userList: List<GitHubUser>)

    @Query("DELETE FROM github_user_table")
    suspend fun clearAll(): Int

    @Query("SELECT * FROM github_user_table")
    fun getPagingSource(): PagingSource<Int, GitHubUser>
}