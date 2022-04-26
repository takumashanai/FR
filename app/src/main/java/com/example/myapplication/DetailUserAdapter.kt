package com.example.myapplication

import android.content.Intent
import android.graphics.Outline
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemDetailBinding

class DetailUserAdapter(
    diffCallback: DiffUtil.ItemCallback<DetailUser>
    ): ListAdapter<DetailUser,DetailUserAdapter.DetailUserViewHolder>(diffCallback) {

    class DetailUserViewHolder(
        private val binding: ItemDetailBinding
    ): RecyclerView.ViewHolder(binding.root) {
        private val activity = binding.root.context as AppCompatActivity
        private val text1 = binding.textView1
        private val text2 = binding.textView2
        private val text3 = binding.textView3
        private val text4 = binding.textView4
        private val image1 = binding.imageView1
        fun bind(item: DetailUser){
            item.title?.let{ it ->
                val title = text1.context.getString(R.string.item,it)
                text1.text = title
                text1.setOnClickListener {
                    item.html?.let{ url ->
                        openUrl(url)
                    }
                }
            }
            item.description?.let{
                val description = text2.context.getString(R.string.description,it)
                text2.text = description
            }
            item.star?.let{
                text3.text = it.toString()
            }
            item.homepage?.let { url ->
                image1.setOnClickListener{
                    openUrl(url)
                }
            }
            image1.outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    outline.setRoundRect(0, 0, view.width, view.height, 10f)
                }
            }
            image1.clipToOutline = true
            item.star?.let{
                text4.text = it.toString()
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

    object UserComparator: DiffUtil.ItemCallback<DetailUser>() {
        override fun areItemsTheSame(oldItem: DetailUser, newItem: DetailUser): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DetailUser, newItem: DetailUser): Boolean =
            oldItem == newItem
    }
}