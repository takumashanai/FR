package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.GithubUserLoadStateViewItemBinding

class GitHubLoadStateAdapter(
    private val retry: () -> Unit
): LoadStateAdapter<GitHubLoadStateAdapter.GitHubUserLoadStateViewHolder>() {

    class GitHubUserLoadStateViewHolder(
        private val binding: GithubUserLoadStateViewItemBinding,
        retry: () -> Unit
    ): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.retryButton.setOnClickListener{ retry.invoke() }
        }
        fun bind(loadState: LoadState){
            if (loadState is LoadState.Error) {
                binding.errorMsg.text = loadState.error.localizedMessage
            }
            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.retryButton.isVisible = loadState is LoadState.Error
            binding.errorMsg.isVisible = loadState is LoadState.Error
        }

        companion object {
            fun create(parent: ViewGroup, retry: () -> Unit): GitHubUserLoadStateViewHolder {
                val binding = GithubUserLoadStateViewItemBinding.inflate(
                    LayoutInflater.from(parent.context),parent,false)
                return GitHubUserLoadStateViewHolder(binding, retry)
            }
        }
    }

    override fun onBindViewHolder(holder: GitHubUserLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): GitHubUserLoadStateViewHolder {
        return GitHubUserLoadStateViewHolder.create(parent, retry)
    }
}