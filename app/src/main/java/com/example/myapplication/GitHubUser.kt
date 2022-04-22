package com.example.myapplication

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "github_user_table")
data class GitHubUser(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id : Long,
    @ColumnInfo(name = "login")
    val login : String,
    @ColumnInfo(name = "node_id")
    var node_id : String,
    @ColumnInfo(name = "avatar_url")
    var avatar_url : String,
    @ColumnInfo(name = "html_url")
    var html_url : String,
    @ColumnInfo(name = "")
    var repos_url : String,
)