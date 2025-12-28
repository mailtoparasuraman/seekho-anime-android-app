package com.seekho.anime.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.seekho.anime.databinding.ItemAnimeBinding
import com.seekho.anime.core.database.AnimeEntity
import com.seekho.anime.data.model.Anime

class AnimeAdapter(
    private val onItemClick: (AnimeEntity) -> Unit
) : ListAdapter<AnimeEntity, AnimeAdapter.AnimeViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val binding = ItemAnimeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AnimeViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AnimeViewHolder(
        private val binding: ItemAnimeBinding,
        private val onItemClick: (AnimeEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(anime: AnimeEntity) {
            binding.tvTitle.text = anime.title
            // Use episodes from entity
            binding.tvEpisodes.text = if (anime.episodes != null) "Episodes: ${anime.episodes}" else "Episodes: N/A"
            
            // Format score
            val scoreText = if (anime.score != null) "⭐ ${anime.score}" else "⭐ N/A"
            binding.tvRating.text = scoreText

            // Handle No Image Constraint implicitly by layout or placeholder if needed
            // For now, if imageUrl is empty, Glide handles placeholder if set, or we can hide.
            if (anime.imageUrl.isNotEmpty()) {
                binding.ivPoster.visibility = android.view.View.VISIBLE
                Glide.with(binding.ivPoster.context)
                    .load(anime.imageUrl)
                    .placeholder(com.seekho.anime.R.drawable.ic_launcher_background)
                    .error(com.seekho.anime.R.drawable.ic_launcher_background)
                    .into(binding.ivPoster)
            } else {
                 // Design constraint: Handle no image
                binding.ivPoster.visibility = android.view.View.GONE
            }

            binding.root.setOnClickListener {
                onItemClick(anime)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<AnimeEntity>() {
        override fun areItemsTheSame(oldItem: AnimeEntity, newItem: AnimeEntity): Boolean {
            return oldItem.mal_id == newItem.mal_id
        }

        override fun areContentsTheSame(oldItem: AnimeEntity, newItem: AnimeEntity): Boolean {
            return oldItem == newItem
        }
    }
}
