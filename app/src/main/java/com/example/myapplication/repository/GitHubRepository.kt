package com.example.myapplication.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.myapplication.remotemediator.GitHubRemoteMediator
import com.example.myapplication.data.GitHubUser
import com.example.myapplication.api.GitHubUserAPI
import com.example.myapplication.db.AppDatabase
import kotlinx.coroutines.flow.Flow

class GitHubRepository(
    private val service: GitHubUserAPI,
    private val database: AppDatabase
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getSearchResultStream(query: Int): Flow<PagingData<GitHubUser>> {

        val pagingSourceFactory = {
            database.gitHubDao().getPagingSource()
        }

        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE,enablePlaceholders = true),
            remoteMediator = GitHubRemoteMediator(
                service,
                database,
                query
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }
    companion object {
        const val NETWORK_PAGE_SIZE = 30
    }
}