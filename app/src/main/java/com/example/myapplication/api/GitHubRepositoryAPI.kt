package com.example.myapplication.api

import com.example.myapplication.data.GitHubRepositoryResponse
import retrofit2.Call
import retrofit2.http.*

interface GitHubRepositoryAPI {
    @Headers("accept:application/vnd.github.v3+json")
    @GET("users/{login}/repos")
    fun getGitHubRepositoryData(
        @Header("Authorization") accessToken: String?,
        @Path("login") login: String,
        @Query("sort") sort: String,
        @Query("direction") direction: String,
        @Query("per_page") perPage: Int,
        @Query("page") page: Int
    ): Call<Array<GitHubRepositoryResponse>>
}