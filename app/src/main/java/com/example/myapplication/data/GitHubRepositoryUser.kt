package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "github_repository_user_table")
data class GitHubRepositoryUser(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val title: String?,
    val html: String?,
    val homepage: String?,
    val description: String?,
    val star: Float?
)
