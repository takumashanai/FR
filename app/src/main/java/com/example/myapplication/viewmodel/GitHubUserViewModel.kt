package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.*
import com.example.myapplication.data.DetailUser
import com.example.myapplication.data.GitHubUser
import com.example.myapplication.objects.Injection
import com.example.myapplication.repository.GitHubRepository
import kotlinx.coroutines.flow.*

//Application 第一引数, SavedStateHandle 第二引数
class GitHubUserViewModel(
    application: Application,
    private val state: SavedStateHandle
): AndroidViewModel(application) {
    private lateinit var pagingDataFlow: Flow<PagingData<GitHubUser>>
    private val repository: GitHubRepository = Injection.provideGitHubRepository(context = application)
    val login: String? get() = state.get("LOGIN")
    val html: String? get() = state.get("HTML")
    val avatar: String? get() = state.get("AVATAR")
    val detailUserList: MutableLiveData<ArrayList<DetailUser>>? by lazy {
        MutableLiveData<ArrayList<DetailUser>>()
    }

    fun setLogin(login: String?){
        state["LOGIN"] = login
    }

    fun setHtml(html: String?){
        state["HTML"] = html
    }

    fun setAvatar(avatar: String?){
        state["AVATAR"] = avatar
    }

    fun searchGitHubUser(query: Int): Flow<PagingData<GitHubUser>>{
        pagingDataFlow = repository.getSearchResultStream(query).cachedIn(viewModelScope)
        return pagingDataFlow
    }

}