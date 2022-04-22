package com.example.myapplication

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface GitHubAPI {
    @Headers("accept: application/vnd.github.v3+json")
    @GET("users")
    fun getGitHubUserData(
        @Query("since") since : Int,
        @Query("per_page") perPage : Int
    ): Response<List<GitHubUser>>
}