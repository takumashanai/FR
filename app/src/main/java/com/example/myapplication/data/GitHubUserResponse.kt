package com.example.myapplication.data

import com.google.gson.annotations.SerializedName

data class GitHubUserResponse(
    @SerializedName("id") val id : Long,
    @SerializedName("login") val login : String,
    @SerializedName("avatar_url") val avatar : String,
    @SerializedName("html_url") val html : String,
    @SerializedName("repos_url") val repos : String
)
