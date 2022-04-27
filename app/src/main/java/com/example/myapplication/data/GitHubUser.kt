package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "github_user_table")
data class GitHubUser(
    @PrimaryKey
    @field:SerializedName("id") val id : Long,
    @field:SerializedName("login") val login : String,
    @field:SerializedName("avatar_url") val avatar : String,
    @field:SerializedName("html_url") val html : String,
    @field:SerializedName("repos_url") val repos : String
)