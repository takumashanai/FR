package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

class InventoryViewModel(
    private val itemDao: GitHubDao
    ) : ViewModel() {
    val pagingDataFlow: Flow<PagingData<GitHubUser>>

    init {
        
    }
}

class InventoryViewModelFactory(private val itemDao: GitHubDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InventoryViewModel(itemDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}