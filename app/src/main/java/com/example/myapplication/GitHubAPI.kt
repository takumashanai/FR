package com.example.myapplication

import retrofit2.http.GET

interface GitHubAPI {
    @GET("api/v2/users/myself")
    fun myself(): Call<List<GitHubUser>>
}