package com.example.myapplication.api

import com.example.myapplication.data.GitHubUserFFResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path

interface GitHubUserFFAPI {
    @Headers("accept:application/vnd.github.v3+json")
    @GET("users/{login}")
    fun getGitHubUserFFData(
        @Header("Authorization") accessToken: String?,
        @Path("login") login: String
    ): Call<GitHubUserFFResponse>
}