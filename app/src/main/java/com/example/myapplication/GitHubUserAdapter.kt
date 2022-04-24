package com.example.myapplication

import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
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
    diffCallback: DiffUtil.ItemCallback<GitHubUser>
): PagingDataAdapter<GitHubUser, GitHubUserAdapter.GitHubUserViewHolder>(
    diffCallback
) {
    class GitHubUserViewHolder(
        binding: ItemMainBinding
    ): RecyclerView.ViewHolder(binding.root) {
        private val text = binding.textView1
        private val image = binding.imageView1
        private val card = binding.cardView1
        private val repositoryAPI by lazy {
            retrofit.create(GitHubRepositoryAPI::class.java)
        }
        fun bind(item: GitHubUser?){
            item?.login?.let{
                val name = text.context.getString(R.string.name,it)
                text.text = name
            }
            item?.avatar?.let{
                Glide.with(image.context)
                    .load(it)
                    .circleCrop()
                    .into(image)
            }
            card.setOnClickListener{
                item?.login?.let {

                    repositoryAPI.getGitHubRepositoryData(it,"updated","desc",100,1)
                        .enqueue(object : Callback<Array<GitHubRepositoryResponse>>{
                        override fun onFailure(call: Call<Array<GitHubRepositoryResponse>>?, t: Throwable?) {
                            Log.d("fetchItems", "response fail")
                            Log.d("fetchItems", "throwable :$t")
                        }

                        override fun onResponse(call: Call<Array<GitHubRepositoryResponse>>?, response: Response<Array<GitHubRepositoryResponse>>) {
                            if (response.isSuccessful) {
                                response.body()?.let {

                                }
                            }
                        }
                    })
                }
            }
        }
    }

    override fun onBindViewHolder(holder: GitHubUserViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GitHubUserViewHolder {
        return GitHubUserViewHolder(ItemMainBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    object UserComparator: DiffUtil.ItemCallback<GitHubUser>() {
        override fun areItemsTheSame(oldItem: GitHubUser, newItem: GitHubUser): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GitHubUser, newItem: GitHubUser): Boolean =
            oldItem == newItem
    }

}