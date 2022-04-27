package com.example.myapplication.objects

import android.content.Context
import com.example.myapplication.objects.RetrofitInstance.retrofit
import com.example.myapplication.api.GitHubUserAPI
import com.example.myapplication.db.AppDatabase
import com.example.myapplication.repository.GitHubRepository

object Injection {
    fun provideGitHubRepository(context: Context): GitHubRepository {
        return GitHubRepository(retrofit.create(GitHubUserAPI::class.java), AppDatabase.getDatabase(context))
    }
}