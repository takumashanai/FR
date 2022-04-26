package com.example.myapplication

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GitHubUserViewModel(
    application: Application
): AndroidViewModel(application) {
    private lateinit var pagingDataFlow: Flow<PagingData<GitHubUser>>
    private val repository: GitHubRepository = Injection.provideGitHubRepository(context = application)
    private var _login: String? = null
    val login: String?
        get() = _login
    private var _html: String? = null
    val html: String?
        get() = _html
    private var _avatar: String? = null
    val avatar: String?
        get() = _avatar
    val detailUserList: MutableLiveData<ArrayList<DetailUser>>? by lazy {
        MutableLiveData<ArrayList<DetailUser>>()
    }


    fun setLogin(login: String?){
        _login = login
    }

    fun setHtml(html: String?){
        _html = html
    }

    fun setAvatar(avatar: String?){
        _avatar = avatar
    }

    fun searchGitHubUser(query: Int): Flow<PagingData<GitHubUser>>{
        pagingDataFlow = repository.getSearchResultStream(query).cachedIn(viewModelScope)
        return pagingDataFlow
    }

}