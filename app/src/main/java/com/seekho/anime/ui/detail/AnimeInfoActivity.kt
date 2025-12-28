package com.seekho.anime.ui.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.seekho.anime.databinding.ActivityAnimeDetailBinding


import com.seekho.anime.core.di.AnimeViewModelFactory
import com.seekho.anime.core.di.MyApplication

class AnimeInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnimeDetailBinding
    private val viewModel: AnimeInfoViewModel by viewModels {
        AnimeViewModelFactory((application as MyApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnimeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener {
             finish()
        }

        val animeId = intent.getIntExtra("ANIME_ID", -1)

        if (animeId != -1) {
            viewModel.fetchAnimeDetail(animeId)
        }

        setupObservers()
    }

    private lateinit var castAdapter: CastAdapter

    private fun setupObservers() {

        // Initialize adapter
        castAdapter = CastAdapter()
        binding.castRecyclerView.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@AnimeInfoActivity, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
            adapter = castAdapter
        }

        viewModel.animeDetail.observe(this) { anime ->
            binding.titleText.text = anime.title
            binding.synopsisText.text = anime.synopsis ?: "No synopsis available"
            binding.episodeCountText.text = "Episodes: ${anime.episodes ?: "N/A"}"
            binding.scoreText.text = "${anime.score ?: "N/A"}"
            binding.scoreText.setCompoundDrawablesWithIntrinsicBounds(com.seekho.anime.R.drawable.ic_star, 0, 0, 0)
            binding.scoreText.compoundDrawablePadding = 8
            binding.genreListText.text = anime.genres
            
            // Submit cast list
            castAdapter.submitList(anime.cast)

            anime.youtubeVideoId?.let { videoId ->
                displayTrailer(videoId)
            } ?: run {
                displayPoster(anime.imageUrl)
            }
        }

        viewModel.error.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }
    }

    private fun displayTrailer(videoId: String) {
        binding.trailerWebView.visibility = View.VISIBLE
        binding.coverImage.visibility = View.GONE

        binding.trailerWebView.settings.javaScriptEnabled = true
        binding.trailerWebView.loadUrl("https://www.youtube.com/embed/$videoId")
    }

    private fun displayPoster(imageUrl: String) {
        binding.trailerWebView.visibility = View.GONE
        binding.coverImage.visibility = View.VISIBLE

        Glide.with(this)
            .load(imageUrl)
            .into(binding.coverImage)
    }
}
