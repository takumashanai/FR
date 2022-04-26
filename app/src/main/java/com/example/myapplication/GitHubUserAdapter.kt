package com.example.myapplication

import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ItemMainBinding
import com.example.myapplication.RetrofitInstance.retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
                val name = text1.context.getString(R.string.name,it)
                text1.text = name
            }
            item?.avatar?.let{
                Glide.with(image.context)
                    .load(it)
                    .circleCrop()
                    .into(image)
            }
            item?.html?.let { it ->
                val url = text2.context.getString(R.string.link,it)
                text2.text = url
                text2.setOnClickListener { v ->
                    openUrl(it)
                }
            }
            card.setOnClickListener{
                itemClickListener.onItemClick(item?.login,item?.html,item?.avatar)
                val detailFragment = DetailFragment()
                val transaction = activity.supportFragmentManager.beginTransaction()
                transaction
                    .replace(R.id.container_main_activity,detailFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        private fun openUrl(url: String){
            var webpage = Uri.parse(url)
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                webpage = Uri.parse("http://$url");
            }
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = webpage
            activity.startActivity(intent)
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