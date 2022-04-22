package com.example.myapplication

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator


@ExperimentalPagingApi
class GitHubRemoteMediator : RemoteMediator<Int, User>(){
    override suspend fun load(loadType: LoadType, state: PagingState<Int, User>): MediatorResult {
        TODO("Not yet implemented")
    }
}