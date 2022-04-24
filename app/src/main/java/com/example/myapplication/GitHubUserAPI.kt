package com.example.myapplication

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface GitHubUserAPI {
    @Headers("accept:application/vnd.github.v3+json")
    @GET("users")
    suspend fun getGitHubUserData(
        @Query("since") since : Int,
        @Query("per_page") perPage : Int
    ): Array<GitHubUserResponse>
}