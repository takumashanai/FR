package com.example.myapplication.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.data.GitHubUser
import com.example.myapplication.adapter.GitHubLoadStateAdapter
import com.example.myapplication.adapter.GitHubUserAdapter
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.viewmodel.GitHubUserViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), GitHubUserAdapter.ItemClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var pagingData: Flow<PagingData<GitHubUser>>
    private val viewModel: GitHubUserViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pagingData = viewModel.searchGitHubUser(DEFAULT_QUERY)
        binding.bindState(
            pagingData = pagingData
        )
    }

    private fun ActivityMainBinding.bindState(
        pagingData: Flow<PagingData<GitHubUser>>
    ){
        val adapter = GitHubUserAdapter(GitHubUserAdapter.UserComparator,this@MainActivity)
        val loadState = GitHubLoadStateAdapter{ adapter.retry() }
        recyclerView1.adapter = adapter.withLoadStateHeaderAndFooter(
            header = loadState,
            footer = loadState
        )
        swipe1.setOnRefreshListener {
            adapter.refresh()
        }
        recyclerView1.layoutManager = GridLayoutManager(
            this@MainActivity,2,GridLayoutManager.VERTICAL,false
        )
        bindList(
            userLoadState = loadState,
            adapter = adapter,
            pagingData = pagingData
        )
    }

    private fun ActivityMainBinding.bindList(
        userLoadState: GitHubLoadStateAdapter,
        adapter: GitHubUserAdapter,
        pagingData: Flow<PagingData<GitHubUser>>
    ) {
        button1.setOnClickListener { adapter.retry() }
        lifecycleScope.launch {
            pagingData.collectLatest(adapter::submitData)
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collect{ loadState ->
                userLoadState.loadState = loadState.mediator
                    ?.refresh
                    ?.takeIf { it is LoadState.Error && adapter.itemCount > 0 }
                    ?: loadState.prepend
                val isListEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
                textView1.isVisible = isListEmpty
                recyclerView1.isVisible =  loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading
                progressBar1.isVisible = loadState.mediator?.refresh is LoadState.Loading
                button1.isVisible = loadState.mediator?.refresh is LoadState.Error && adapter.itemCount == 0
                swipe1.isRefreshing = loadState.source.refresh is LoadState.Loading || loadState.mediator?.refresh is LoadState.Loading
                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    Toast.makeText(
                        this@MainActivity,
                        "${it.error}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onItemClick(login: String?, html: String?, avatar: String?) {
        if (login != null) {
            viewModel.setLogin(login)
        }
        if (html != null) {
            viewModel.setHtml(html)
        }
        if (avatar != null) {
            viewModel.setAvatar(avatar)
        }
    }
}
private const val DEFAULT_QUERY = 1
