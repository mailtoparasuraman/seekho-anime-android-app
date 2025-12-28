package com.seekho.anime.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.seekho.anime.core.database.CastItemEntity
import com.seekho.anime.databinding.ItemCastBinding

class CastAdapter : ListAdapter<CastItemEntity, CastAdapter.CastViewHolder>(CastDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        val binding = ItemCastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CastViewHolder(private val binding: ItemCastBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CastItemEntity) {
            binding.castName.text = item.name
            binding.castRole.text = item.role
            
            Glide.with(itemView.context)
                .load(item.imageUrl)
                .into(binding.castImage)
        }
    }

    class CastDiffCallback : DiffUtil.ItemCallback<CastItemEntity>() {
        override fun areItemsTheSame(oldItem: CastItemEntity, newItem: CastItemEntity): Boolean {
            return oldItem.name == newItem.name // Assuming name is unique enough for display purposes
        }

        override fun areContentsTheSame(oldItem: CastItemEntity, newItem: CastItemEntity): Boolean {
            return oldItem == newItem
        }
    }
}
