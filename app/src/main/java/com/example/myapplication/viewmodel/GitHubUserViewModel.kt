package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.*
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
    val repos: String? get() = state.get("REPOS")
    val followers: String? get() = state.get("FOLLOWERS")
    val language: MutableLiveData<String?> get() = state.getLiveData<String?>("LANGUAGE",null)
    val colorNum: MutableLiveData<Int?> get() = state.getLiveData<Int?>("COLOR_NUM",null)

    fun setLogin(login: String?){
        state["LOGIN"] = login
    }

    fun setHtml(html: String?){
        state["HTML"] = html
    }

    fun setAvatar(avatar: String?){
        state["AVATAR"] = avatar
    }

    fun setRepos(repos: String?){
        state["REPOS"] = repos
    }

    fun setFollowers(followers: String?){
        state["FOLLOWERS"] = followers
    }

    fun setColorNum(colorNum: Int?){
        state["COLOR_NUM"] = colorNum
    }

    fun setLanguage(language: String?){
        state["LANGUAGE"] = language
    }

    fun searchGitHubUser(query: Int): Flow<PagingData<GitHubUser>>{
        pagingDataFlow = repository.getSearchResultStream(query).cachedIn(viewModelScope)
        return pagingDataFlow
    }

}