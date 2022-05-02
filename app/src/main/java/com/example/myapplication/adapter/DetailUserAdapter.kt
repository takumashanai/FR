package com.example.myapplication.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.GitHubRepositoryUser
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemDetailBinding
import kotlin.math.round

class DetailUserAdapter(
    diffCallback: DiffUtil.ItemCallback<GitHubRepositoryUser>
    ): ListAdapter<GitHubRepositoryUser, DetailUserAdapter.DetailUserViewHolder>(diffCallback) {

    class DetailUserViewHolder(
        binding: ItemDetailBinding
    ): RecyclerView.ViewHolder(binding.root) {
        private val activity = binding.root.context as AppCompatActivity
        private val text1 = binding.textView1
        private val text2 = binding.textView2
        private val text3 = binding.textView3
        private val image1 = binding.imageView1
        fun bind(item: GitHubRepositoryUser){
            item.title?.let{ it ->
                val title = text1.context.getString(R.string.item,it)
                text1.text = title
                text1.setOnClickListener {
                    item.html?.let{ url ->
                        openUrl(url)
                    }
                }
            }
            item.description?.let {
                val description = activity.resources.getString(R.string.description,it)
                text2.text = description
            } ?: let {
                val description = activity.resources.getString(R.string.description,"None stated")
                text2.text = description
            }

            item.homepage?.let { url ->
                if (url.isBlank()) image1.visibility = View.INVISIBLE else {
                    image1.visibility = View.VISIBLE
                    image1.clipToOutline = true
                    image1.setOnClickListener{
                        openUrl(url)
                    }
                }
            } ?: let {
                image1.visibility = View.INVISIBLE
            }
            item.star?.let{
                text3.text = getRepString(it)
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

        private fun getRepString(rep: Float): String{
            return when {
                (rep < 10000) -> "${rep.toInt()}"
                (rep < 1000000) -> "${round((rep / 1000))}k"
                else -> "${round((rep / 1000000))}m"
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailUserViewHolder {
        return DetailUserViewHolder(ItemDetailBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: DetailUserViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    object UserComparator: DiffUtil.ItemCallback<GitHubRepositoryUser>() {
        override fun areItemsTheSame(oldItem: GitHubRepositoryUser, newItem: GitHubRepositoryUser): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GitHubRepositoryUser, newItem: GitHubRepositoryUser): Boolean =
            oldItem == newItem
    }
}