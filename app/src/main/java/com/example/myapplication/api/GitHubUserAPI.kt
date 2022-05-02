package com.example.myapplication.api

import com.example.myapplication.data.GitHubUserResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface GitHubUserAPI {
    @Headers("accept:application/vnd.github.v3+json")
    @GET("users")
    suspend fun getGitHubUserData(
        @Header("Authorization") accessToken: String?,
        @Query("since") since : Int,
        @Query("per_page") perPage : Int
    ): Response<Array<GitHubUserResponse>>
}