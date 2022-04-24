package com.example.myapplication

import com.google.gson.annotations.SerializedName

data class GitHubRepositoryResponse(
    @SerializedName("description") val description : String,
    @SerializedName("url") val url : String,
    @SerializedName("updated_at") val update : String,
    @SerializedName("language") val language : String,
    @SerializedName("homepage") val homepage : String,
    @SerializedName("stargazers_count") val star : Int
)
