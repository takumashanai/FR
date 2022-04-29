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
    //private var _login: String? = null
    val login: String? get() = state.get("login")
    //private var _html: String? = state.get("html")
    val html: String? get() = state.get("html")
    //private var _avatar: String? = null
    val avatar: String? get() = state.get("avatar")
    val detailUserList: MutableLiveData<ArrayList<DetailUser>>? by lazy {
        MutableLiveData<ArrayList<DetailUser>>()
    }

    fun setLogin(login: String?){
        state["login"] = login
    }

    fun setHtml(html: String?){
        state["html"] = html
    }

    fun setAvatar(avatar: String?){
        state["avatar"] = avatar
    }

    fun searchGitHubUser(query: Int): Flow<PagingData<GitHubUser>>{
        pagingDataFlow = repository.getSearchResultStream(query).cachedIn(viewModelScope)
        return pagingDataFlow
    }

}