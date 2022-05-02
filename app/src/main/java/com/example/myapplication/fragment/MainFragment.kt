package com.example.myapplication.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapter.GitHubLoadStateAdapter
import com.example.myapplication.adapter.GitHubUserAdapter
import com.example.myapplication.data.GitHubUser
import com.example.myapplication.databinding.FragmentMainBinding
import com.example.myapplication.db.AppDatabase
import com.example.myapplication.viewmodel.GitHubUserViewModel
import kotlinx.coroutines.flow.*
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
        recyclerView1.layoutManager = LinearLayoutManager(
            activity,LinearLayoutManager.VERTICAL,false
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                pagingData.collectLatest(adapter::submitData)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
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
                    val errorState = loadState.refresh as? LoadState.Error
                        ?: loadState.source.append as? LoadState.Error
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow
                    .distinctUntilChangedBy { it.refresh }
                    .filter { it.refresh is LoadState.NotLoading }
                    .collect { recyclerView1.scrollToPosition(0) }
            }
        }
    }

    override fun onDestroyView(){
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(login: String?, html: String?, avatar: String?,repos: String?,followers: String?) {
        sharedViewModel.setLogin(login)
        sharedViewModel.setHtml(html)
        sharedViewModel.setAvatar(avatar)
        sharedViewModel.setRepos(repos)
        sharedViewModel.setFollowers(followers)
        sharedViewModel.setColorNum(null)
        sharedViewModel.setLanguage(null)
        val database = activity?.let { AppDatabase.getDatabase(it) }
        val dao = database?.gitHubRepositoryDao()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                dao?.clearAll()
            }
        }
        val navHostFragment =
            activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val action = MainFragmentDirections.actionMainFragmentToDetailFragment()
        navController.navigate(action)
    }
}
private const val DEFAULT_QUERY = 1