package com.example.myapplication.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.data.GitHubUser
import com.example.myapplication.R
import com.example.myapplication.Signature
import com.example.myapplication.api.GitHubUserFFAPI
import com.example.myapplication.data.GitHubUserFFResponse
import com.example.myapplication.databinding.ItemMainBinding
import com.example.myapplication.objects.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.round

class GitHubUserAdapter(
    diffCallback: DiffUtil.ItemCallback<GitHubUser>,
    private val itemClickListener: ItemClickListener
): PagingDataAdapter<GitHubUser, GitHubUserAdapter.GitHubUserViewHolder>(
    diffCallback
) {
    private val ffAPI by lazy {
        RetrofitInstance.retrofit.create(GitHubUserFFAPI::class.java)
    }
    interface ItemClickListener {
        fun onItemClick(login: String?,html: String?,avatar: String?,repos: String?,followers: String?)
    }
    class GitHubUserViewHolder(
        binding: ItemMainBinding,
        private val itemClickListener: ItemClickListener,
        private val ffAPI: GitHubUserFFAPI
    ): RecyclerView.ViewHolder(binding.root) {
        private val activity = binding.root.context as AppCompatActivity
        private val text1 = binding.textView1
        private val text2 = binding.textView2
        private val text3 = binding.textView3
        private val text4 = binding.textView4
        private val image1 = binding.imageView1
        private val image2 = binding.imageView2
        private val card = binding.cardView1

        fun bind(item: GitHubUser?){
            image2.clipToOutline = true
            item?.login?.let{ login ->
                text1.text = login
                text3.setOnClickListener {
                    openUrl("https://github.com/${login}?tab=repositories")
                }
                text4.setOnClickListener {
                    openUrl("https://github.com/${login}?tab=followers")
                }
                ffAPI.getGitHubUserFFData(Signature.getAccessToken(activity),login).enqueue(object :Callback<GitHubUserFFResponse>{
                    override fun onResponse(
                        call: Call<GitHubUserFFResponse>,
                        response: Response<GitHubUserFFResponse>
                    ) {
                        if (response.isSuccessful && response.code() == 200) {
                            response.body()?.let { body ->
                                val bio = body.bio
                                if(!bio.isNullOrBlank()){
                                    text2.text = activity.resources.getString(R.string.bio,bio)
                                }
                                text3.text = body.public_repos?.toFloat()
                                    ?.let { it1 -> getRepString(it1) }
                                text4.text = body.followers?.toFloat()
                                    ?.let { it2 -> getRepString(it2) }
                            }
                        }else{
                            text2.text = "?"
                            text3.text = "?"
                            text4.text = "?"
                            Toast.makeText(
                                activity,
                                "${response.code()} error",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<GitHubUserFFResponse>, t: Throwable) {
                        text2.text = "?"
                        text3.text = "?"
                        text4.text = "?"
                        Toast.makeText(
                            activity,
                            "load error",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                })
            }
            item?.avatar?.let{
                Glide.with(image1.context)
                    .load(it)
                    .circleCrop()
                    .into(image1)
            }
            item?.html?.let { it ->
                image2.setOnClickListener { v ->
                    openUrl(it)
                }
            }
            card.setOnClickListener{
                itemClickListener.onItemClick(item?.login,item?.html,item?.avatar,text3.text?.toString(),text4.text?.toString())
            }
        }

        private fun getRepString(rep: Float): String{
            return when {
                (rep < 10000) -> "${rep.toInt()}"
                (rep < 1000000) -> "${round((rep / 1000)).toInt()}k"
                else -> "${round((rep / 1000000)).toInt()}m"
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
        return GitHubUserViewHolder(ItemMainBinding.inflate(LayoutInflater.from(parent.context),parent,false),itemClickListener,ffAPI)
    }

    object UserComparator: DiffUtil.ItemCallback<GitHubUser>() {
        override fun areItemsTheSame(oldItem: GitHubUser, newItem: GitHubUser): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GitHubUser, newItem: GitHubUser): Boolean =
            oldItem == newItem
    }

}