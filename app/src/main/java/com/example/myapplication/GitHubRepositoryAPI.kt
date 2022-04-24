package com.example.myapplication

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubRepositoryAPI {
    @Headers("accept:application/vnd.github.v3+json")
    @GET("users/{login}/repos")
    fun getGitHubRepositoryData(
        @Path("login") login: String,
        @Query("sort") sort: String,
        @Query("direction") direction: String,
        @Query("per_page") perPage: Int,
        @Query("page") page: Int
    ): Call<Array<GitHubRepositoryResponse>>
}