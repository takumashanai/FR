package com.example.myapplication

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import retrofit2.HttpException
import java.io.IOException


@ExperimentalPagingApi
class GitHubRemoteMediator(
    private val networkService: GitHubAPI,
    private val database: AppDatabase,
    private val since: Int
) : RemoteMediator<Int, GitHubUser>() {
    private var loadKeyVault : Int = 1
    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, GitHubUser>
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> loadKeyVault
                LoadType.PREPEND ->
                    return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) {
                        return MediatorResult.Success(
                            endOfPaginationReached = true
                        )
                    }
                    loadKeyVault += 1
                    loadKeyVault
                }
            }

            val response = networkService.getGitHubUserData(
                since = this.since, perPage = loadKeyVault
            )

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.gitHubDao().clearAll()
                }

                response.body()?.let { database.gitHubDao().insertAll(it) }
            }

            MediatorResult.Success(
                endOfPaginationReached = response.body().isNullOrEmpty()
            )
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}