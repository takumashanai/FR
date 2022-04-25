package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemDetailBinding

class DetailUserAdapter(
    private val list: ArrayList<ArrayList<String>>
    ): RecyclerView.Adapter<DetailUserAdapter.DetailUserViewHolder>() {

    class DetailUserViewHolder(
        binding: ItemDetailBinding
    ): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailUserViewHolder {
        return DetailUserViewHolder(ItemDetailBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: DetailUserViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return list.size
    }


}