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
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) {
                        return MediatorResult.Success(
                            endOfPaginationReached = true
                        )
                    }
                    since += 100
                    since
                }
            }

            val response = networkService.getGitHubUserData(
                since = loadKey, perPage = 100
            )
            val endOfPaginationReached = response.isNullOrEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.gitHubDao().clearAll()
                }
                response.forEach { it ->
                    database.gitHubDao().insert(
                        GitHubUser(
                            id = it.id,
                            login = it.login,
                            node = it.node,
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
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}