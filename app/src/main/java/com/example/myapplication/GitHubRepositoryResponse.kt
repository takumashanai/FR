package com.example.myapplication

import com.google.gson.annotations.SerializedName

data class GitHubRepositoryResponse(
    @SerializedName("id") val id : Int,
    @SerializedName("name") val title : String,
    @SerializedName("description") val description : String,
    @SerializedName("url") val url : String,
    @SerializedName("updated_at") val update : String,
    @SerializedName("language") val language : String,
    @SerializedName("homepage") val homepage : String,
    @SerializedName("html_url") val html : String,
    @SerializedName("stargazers_count") val star : Int
)
