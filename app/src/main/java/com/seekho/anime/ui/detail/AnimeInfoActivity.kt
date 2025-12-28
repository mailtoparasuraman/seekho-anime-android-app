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

    private var animeId: Int = -1

    @androidx.annotation.RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnimeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener {
             finish()
        }

        animeId = intent.getIntExtra("ANIME_ID", -1)

        if (animeId != -1) {
            viewModel.fetchAnimeDetail(animeId)
        }

        setupObservers()
        setupNetworkCallback()
    }

    private lateinit var connectivityManager: android.net.ConnectivityManager
    private lateinit var networkCallback: android.net.ConnectivityManager.NetworkCallback

    @androidx.annotation.RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    private fun setupNetworkCallback() {
        connectivityManager = getSystemService(android.net.ConnectivityManager::class.java)
        val networkRequest = android.net.NetworkRequest.Builder()
            .addCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
            
        networkCallback = object : android.net.ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: android.net.Network) {
                super.onAvailable(network)
                runOnUiThread {
                     if (!isDestroyed) {
                         // Check if data is missing or incomplete (cast is null) to decide if we should refresh.
                         if (animeId != -1) {
                             if (viewModel.animeDetail.value == null || viewModel.animeDetail.value?.cast == null) {
                                  Toast.makeText(this@AnimeInfoActivity, "Back online. Refreshing...", Toast.LENGTH_SHORT).show()
                             }
                             viewModel.fetchAnimeDetail(animeId)
                         }
                     }
                }
            }
        }
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::networkCallback.isInitialized && ::connectivityManager.isInitialized) {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }

    private lateinit var castAdapter: CastAdapter

    private fun setupObservers() {

        // Initialize adapter
        castAdapter = CastAdapter()
        binding.castListRecycler.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@AnimeInfoActivity, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
            adapter = castAdapter
        }

        viewModel.animeDetail.observe(this) { anime ->
            binding.errorStateMessage.visibility = View.GONE
            binding.detailScrollView.visibility = View.VISIBLE

            binding.animeTitle.text = anime.title
            binding.animeSynopsis.text = anime.synopsis ?: "No synopsis available"
            binding.episodeCountText.text = "Episodes: ${anime.episodes ?: "N/A"}"
            binding.animeScore.text = "${anime.score ?: "N/A"}"
            binding.animeScore.setCompoundDrawablesWithIntrinsicBounds(com.seekho.anime.R.drawable.ic_star, 0, 0, 0)
            binding.animeScore.compoundDrawablePadding = 8
            binding.animeGenres.text = anime.genres
            
            // Handle Cast Section Logic
            // If cast is null, we are likely still loading it (background fetch).
            if (anime.cast == null) {
                // Loading State logic
                binding.castSectionTitle.visibility = View.VISIBLE
                binding.castLoadingProgress.visibility = View.VISIBLE
                binding.castListRecycler.visibility = View.GONE
            } else {
                // Data Loaded State
                binding.castSectionTitle.visibility = View.VISIBLE
                binding.castLoadingProgress.visibility = View.GONE
                
                if (anime.cast.isNotEmpty()) {
                    binding.castListRecycler.visibility = View.VISIBLE
                    castAdapter.submitList(anime.cast)
                } else {
                     binding.castListRecycler.visibility = View.GONE
                }
            }

            anime.youtubeVideoId?.let { videoId ->
                displayTrailer(videoId)
            } ?: run {
                displayPoster(anime.imageUrl)
            }
        }

        viewModel.error.observe(this) { errorMsg ->
            if (viewModel.animeDetail.value == null) {
                // No data visible, show error in center view
                binding.errorStateMessage.text = errorMsg
                binding.errorStateMessage.visibility = View.VISIBLE
                binding.detailScrollView.visibility = View.GONE
            } else {
                // Data visible.
                
                // If we were waiting for cast (cast is null) and an error occurred, stop the loading spinner.
                if (viewModel.animeDetail.value?.cast == null) {
                    binding.castLoadingProgress.visibility = View.GONE
                }
                
                // Show Toast to indicate failure
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
            }
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
            .placeholder(com.seekho.anime.R.drawable.ic_image_placeholder)
            .error(com.seekho.anime.R.drawable.ic_image_placeholder)
            .into(binding.coverImage)
    }
}
