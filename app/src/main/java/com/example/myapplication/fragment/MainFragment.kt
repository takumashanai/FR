package com.example.myapplication.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.adapter.GitHubLoadStateAdapter
import com.example.myapplication.adapter.GitHubUserAdapter
import com.example.myapplication.data.GitHubUser
import com.example.myapplication.databinding.FragmentMainBinding
import com.example.myapplication.viewmodel.GitHubUserViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainFragment : Fragment(),GitHubUserAdapter.ItemClickListener {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: GitHubUserViewModel by activityViewModels()
    private lateinit var pagingData: Flow<PagingData<GitHubUser>>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pagingData = sharedViewModel.searchGitHubUser(DEFAULT_QUERY)
        binding.bindState(
            pagingData = pagingData
        )
    }

    private fun FragmentMainBinding.bindState(
        pagingData: Flow<PagingData<GitHubUser>>
    ){
        val adapter = GitHubUserAdapter(GitHubUserAdapter.UserComparator,this@MainFragment)
        val loadState = GitHubLoadStateAdapter{ adapter.retry() }
        recyclerView1.adapter = adapter.withLoadStateHeaderAndFooter(
            header = loadState,
            footer = loadState
        )
        swipe1.setOnRefreshListener {
            adapter.refresh()
        }
        recyclerView1.layoutManager = GridLayoutManager(
            activity,2, GridLayoutManager.VERTICAL,false
        )
        bindList(
            userLoadState = loadState,
            adapter = adapter,
            pagingData = pagingData
        )
    }

    private fun FragmentMainBinding.bindList(
        userLoadState: GitHubLoadStateAdapter,
        adapter: GitHubUserAdapter,
        pagingData: Flow<PagingData<GitHubUser>>
    ) {
        button1.setOnClickListener { adapter.retry() }
        lifecycleScope.launch {
            pagingData.collectLatest(adapter::submitData)
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->
                userLoadState.loadState = loadState.mediator
                    ?.refresh
                    ?.takeIf { it is LoadState.Error && adapter.itemCount > 0 }
                    ?: loadState.prepend
                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
                textView1.isVisible = isListEmpty
                recyclerView1.isVisible =
                    loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading
                progressBar1.isVisible = loadState.mediator?.refresh is LoadState.Loading
                button1.isVisible =
                    loadState.mediator?.refresh is LoadState.Error && adapter.itemCount == 0
                swipe1.isRefreshing =
                    loadState.source.refresh is LoadState.Loading || loadState.mediator?.refresh is LoadState.Loading
                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    Toast.makeText(
                        activity,
                        "${it.error}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onDestroyView(){
        super.onDestroyView()
        sharedViewModel.detailUserList?.value = null
        sharedViewModel.setLogin(null)
        sharedViewModel.setAvatar(null)
        sharedViewModel.setHtml(null)
        _binding = null
    }

    override fun onItemClick(login: String?, html: String?, avatar: String?) {
        if (login != null) {
            sharedViewModel.setLogin(login)
        }
        if (html != null) {
            sharedViewModel.setHtml(html)
        }
        if (avatar != null) {
            sharedViewModel.setAvatar(avatar)
        }
    }
}
private const val DEFAULT_QUERY = 1