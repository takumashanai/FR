package com.example.myapplication.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import androidx.transition.TransitionSet
import com.bumptech.glide.Glide
import com.example.myapplication.fragment.DetailFragment
import com.example.myapplication.data.GitHubUser
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemMainBinding
import com.example.myapplication.fragment.MainFragmentDirections

class GitHubUserAdapter(
    diffCallback: DiffUtil.ItemCallback<GitHubUser>,
    private val itemClickListener: ItemClickListener
): PagingDataAdapter<GitHubUser, GitHubUserAdapter.GitHubUserViewHolder>(
    diffCallback
) {
    interface ItemClickListener {
        fun onItemClick(login: String?,html: String?,avatar: String?)
    }
    class GitHubUserViewHolder(
        binding: ItemMainBinding,
        private val itemClickListener: ItemClickListener
    ): RecyclerView.ViewHolder(binding.root) {
        private val activity = binding.root.context as AppCompatActivity
        private val text1 = binding.textView1
        private val text2 = binding.textView2
        private val image = binding.imageView1
        private val card = binding.cardView1

        fun bind(item: GitHubUser?){
            item?.login?.let{
                text1.text = it
            }
            item?.avatar?.let{
                Glide.with(image.context)
                    .load(it)
                    .circleCrop()
                    .into(image)
            }
            item?.html?.let { it ->
                text2.setOnClickListener { v ->
                    openUrl(it)
                }
            }
            card.setOnClickListener{
                itemClickListener.onItemClick(item?.login,item?.html,item?.avatar)
            }
        }

        private fun openUrl(url: String){
            if(url.isNotBlank()) {
                var webpage = Uri.parse(url)
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    webpage = Uri.parse("http://$url")
                }
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = webpage
                activity.startActivity(intent)
            }
        }
    }

    override fun onBindViewHolder(holder: GitHubUserViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GitHubUserViewHolder {
        return GitHubUserViewHolder(ItemMainBinding.inflate(LayoutInflater.from(parent.context),parent,false),itemClickListener)
    }

    object UserComparator: DiffUtil.ItemCallback<GitHubUser>() {
        override fun areItemsTheSame(oldItem: GitHubUser, newItem: GitHubUser): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GitHubUser, newItem: GitHubUser): Boolean =
            oldItem == newItem
    }

}