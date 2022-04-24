package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: GitHubUserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(
            this,Injection.provideViewModelFactory(
                context = this,
                owner = this
            )
        )[GitHubUserViewModel::class.java]
        binding.bindState(
            pagingData = viewModel.searchGitHubUser(DEFAULT_QUERY)
        )
    }

    override fun onStart() {
        super.onStart()
        binding.button1.setOnClickListener{
            binding.editText1.text?.let {
                binding.bindState(
                    pagingData = viewModel.searchGitHubUser(it.toString().toInt())
                )
            }
        }

    }

    override fun onResume() {
        super.onResume()
    }

    private fun ActivityMainBinding.bindState(
        pagingData: Flow<PagingData<GitHubUser>>
    ){
        val adapter = GitHubUserAdapter(GitHubUserAdapter.UserComparator)
        val loadState = GitHubLoadStateAdapter{ adapter.retry() }
        recyclerView1.adapter = adapter.withLoadStateHeaderAndFooter(
            header = loadState,
            footer = loadState
        )
        recyclerView1.layoutManager = LinearLayoutManager(
            this@MainActivity,LinearLayoutManager.VERTICAL,false
        )
        bindList(
            userLoadState = loadState,
            adapter = adapter,
            pagingData = pagingData
        )
        //swipe1.setOnRefreshListener {  }
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
}
private const val DEFAULT_QUERY = 1
