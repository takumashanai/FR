package com.example.myapplication.remotemediator

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.myapplication.ResponseErrorException
import com.example.myapplication.Signature
import com.example.myapplication.data.GitHubUser
import com.example.myapplication.api.GitHubUserAPI
import com.example.myapplication.db.AppDatabase
import retrofit2.HttpException
import java.io.IOException


@ExperimentalPagingApi
class GitHubRemoteMediator(
    private val networkService: GitHubUserAPI,
    private val database: AppDatabase,
    private val since: Int,
    private val context: Context
) : RemoteMediator<Int, GitHubUser>() {
    private var middleLoadKey: Int = 0
    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, GitHubUser>
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> {
                    middleLoadKey = since
                    since
                }
                LoadType.PREPEND ->
                    return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    state.lastItemOrNull()
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = true
                        )
                    middleLoadKey += PER_PAGE
                    middleLoadKey
                }
            }

            val response = networkService.getGitHubUserData(
                accessToken = Signature.getAccessToken(context),since = loadKey, perPage = PER_PAGE
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
                    response.body()?.let {
                        val userList: ArrayList<GitHubUser> = arrayListOf()
                        it.forEach { item ->
                            userList.add(
                                GitHubUser(
                                    id = item.id,
                                    login = item.login,
                                    avatar = item.avatar,
                                    html = item.html,
                                    repos = item.repos
                                )
                            )
                        }
                        if(!userList.isNullOrEmpty()) database.gitHubDao().insertAll(userList)
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
private const val PER_PAGE = 100