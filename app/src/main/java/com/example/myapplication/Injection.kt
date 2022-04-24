package com.example.myapplication

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.example.myapplication.RetrofitInstance.retrofit

object Injection {
    private fun provideGitHubRepository(context: Context): GitHubRepository{
        return GitHubRepository(retrofit.create(GitHubUserAPI::class.java), AppDatabase.getDatabase(context))
    }

    fun provideViewModelFactory(context: Context,owner: SavedStateRegistryOwner):
            ViewModelProvider.Factory{
        val query: Int = 0
        return ViewModelFactory(owner, provideGitHubRepository(context),query)
    }
}