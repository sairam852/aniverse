package com.example.aniverse.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.paging.PagingDataAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.aniverse.R
import com.example.aniverse.config.AppConfig
import com.example.aniverse.databinding.ItemAnimeBinding
import com.example.aniverse.domain.model.Anime

/**
 * PagingDataAdapter for displaying anime items in a RecyclerView.
 *
 * Uses DiffUtil for efficient updates and ViewBinding for item layout.
 */
class AnimePagingAdapter(
    private val onItemClick: (Anime) -> Unit
) : PagingDataAdapter<Anime, AnimePagingAdapter.AnimeViewHolder>(ANIME_DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val binding = ItemAnimeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AnimeViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        getItem(position)?.let { anime -> holder.bind(anime) }
    }

    class AnimeViewHolder(
        private val binding: ItemAnimeBinding,
        private val onItemClick: (Anime) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(anime: Anime) {
            binding.textTitle.text = anime.displayTitle
            binding.textEpisodes.text = anime.formattedEpisodes
            binding.textRating.text = itemView.context.getString(
                R.string.rating_format,
                anime.formattedScore
            )

            if (AppConfig.showImages) {
                Glide.with(binding.imagePoster)
                    .load(anime.imageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(binding.imagePoster)
            } else {
                Glide.with(binding.imagePoster).clear(binding.imagePoster)
                binding.imagePoster.setImageResource(R.drawable.ic_launcher_foreground)
            }

            binding.root.setOnClickListener { onItemClick(anime) }
        }
    }

    companion object {
        private val ANIME_DIFF_CALLBACK = object : DiffUtil.ItemCallback<Anime>() {
            override fun areItemsTheSame(oldItem: Anime, newItem: Anime): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Anime, newItem: Anime): Boolean =
                oldItem == newItem
        }
    }
}
