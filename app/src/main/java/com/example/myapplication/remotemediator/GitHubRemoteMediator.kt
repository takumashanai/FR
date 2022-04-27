package com.example.myapplication.remotemediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.myapplication.ResponseErrorException
import com.example.myapplication.data.GitHubUser
import com.example.myapplication.api.GitHubUserAPI
import com.example.myapplication.db.AppDatabase
import retrofit2.HttpException
import java.io.IOException


@ExperimentalPagingApi
class GitHubRemoteMediator(
    private val networkService: GitHubUserAPI,
    private val database: AppDatabase,
    private var since: Int
) : RemoteMediator<Int, GitHubUser>() {
    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, GitHubUser>
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> since
                LoadType.PREPEND ->
                    return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    state.lastItemOrNull()
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = true
                        )
                    since += 100
                    since
                }
            }

            val response = networkService.getGitHubUserData(
                since = loadKey, perPage = 100
            )
            val endOfPaginationReached = response.body().isNullOrEmpty()

            try {
                if (response.code() != 200) {
                    throw ResponseErrorException((response.code().toString()))
                }
                database.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        database.gitHubDao().clearAll()
                    }
                    response.body()?.forEach {
                        database.gitHubDao().insert(
                            GitHubUser(
                                id = it.id,
                                login = it.login,
                                avatar = it.avatar,
                                html = it.html,
                                repos = it.repos
                            )
                        )
                    }
                }

                MediatorResult.Success(
                    endOfPaginationReached = endOfPaginationReached
                )
            } catch (e: ResponseErrorException){
                MediatorResult.Error(e)
            }
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}