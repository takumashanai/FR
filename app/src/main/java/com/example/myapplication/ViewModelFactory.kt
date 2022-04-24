package com.example.myapplication

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingData
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.flow.Flow

class ViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val repository: GitHubRepository,
    private val query: Int
) : AbstractSavedStateViewModelFactory(owner, null){
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(GitHubUserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GitHubUserViewModel(repository,query) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}