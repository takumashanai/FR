package com.example.myapplication

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GitHubUserViewModel(
    private val repository: GitHubRepository,
    query: Int
): ViewModel() {
    private val pagingDataFlow: Flow<PagingData<GitHubUser>>

    init {
        viewModelScope.launch {

        }
        pagingDataFlow = searchGitHubUser(query)
    }

    fun searchGitHubUser(query: Int): Flow<PagingData<GitHubUser>>{
        return repository.getSearchResultStream(query).cachedIn(viewModelScope)
    }

}