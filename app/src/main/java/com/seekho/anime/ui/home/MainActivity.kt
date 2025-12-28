package com.seekho.anime.ui.home

import android.Manifest
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.view.View
import android.widget.Toast

import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.activity.compose.setContent
import androidx.core.view.size
import androidx.lifecycle.ViewModelProvider
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.seekho.anime.databinding.ActivityMainBinding
import com.seekho.anime.ui.home.AnimeAdapter

import com.seekho.anime.ui.home.AnimeViewModel
import com.seekho.anime.core.di.AnimeViewModelFactory
import com.seekho.anime.core.di.MyApplication
import com.seekho.anime.ui.detail.AnimeInfoActivity


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: AnimeViewModel by viewModels {
        AnimeViewModelFactory((application as MyApplication).repository)
    }
    private lateinit var animeAdapter: AnimeAdapter

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.mainContainer) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.xmlSelectionButton.setOnClickListener {
            binding.selectionContainer.visibility = View.GONE
            binding.contentLayoutXml.visibility = View.VISIBLE
            initializeXmlUi()
        }

        binding.composeSelectionButton.setOnClickListener {
            setContent {
                 com.seekho.anime.ui.compose.ComposeApp(
                     homeViewModel = viewModel,
                     detailViewModelFactory = { animeId ->
                         val factory = AnimeViewModelFactory((application as MyApplication).repository)
                         ViewModelProvider(this, factory).get(com.seekho.anime.ui.detail.AnimeInfoViewModel::class.java).apply {
                             fetchAnimeDetail(animeId)
                         }
                     }
                 )
            }
        }
        setupNetworkCallback()
    }

    /**
     * Initializes the classic View-based UI (XML) components.
     * Sets up the RecyclerView and ViewModel observations.
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    private fun initializeXmlUi() {
        setupAnimeList()
        observeViewModel()
    }

    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    private fun setupNetworkCallback() {
        connectivityManager = getSystemService(ConnectivityManager::class.java)
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
            
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                runOnUiThread {
                     if (!isDestroyed) {
                         // Check if data is missing using ViewModel source of truth
                         val currentData = viewModel.animeList.value?.data
                         if (currentData.isNullOrEmpty()) {
                             Toast.makeText(this@MainActivity, "Syncing data...", Toast.LENGTH_SHORT).show()
                         }
                         viewModel.refresh()
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
    
    private fun setupAnimeList() {
        animeAdapter = AnimeAdapter { anime ->
            openAnimeDetail(anime.mal_id)
        }

        binding.animeContentList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = animeAdapter
        }
    }

    private fun openAnimeDetail(animeId: Int) {
        val intent = Intent(this, AnimeInfoActivity::class.java).apply {
            putExtra("ANIME_ID", animeId)
        }
        startActivity(intent)
    }

    private fun observeViewModel() {
        viewModel.animeList.observe(this) { resource ->
            when (resource) {
                 is com.seekho.anime.util.Resource.Loading -> {
                     binding.loadingIndicator.visibility = View.VISIBLE
                     binding.errorMessageView.visibility = View.GONE
                     resource.data?.let { animeAdapter.submitList(it) }
                 }
                 is com.seekho.anime.util.Resource.Success -> {
                     binding.loadingIndicator.visibility = View.GONE
                     binding.errorMessageView.visibility = View.GONE
                     binding.animeContentList.visibility = View.VISIBLE
                     animeAdapter.submitList(resource.data)
                 }
                 is com.seekho.anime.util.Resource.Error -> {
                     binding.loadingIndicator.visibility = View.GONE
                     
                     // We use the data from the error resource or existing data
                     val data = resource.data
                     if (data != null) {
                         animeAdapter.submitList(data)
                     }
                     
                     // Check if effective data is empty
                     if (data.isNullOrEmpty()) {
                         // No data: Show Error Screen
                         binding.errorMessageView.text = resource.error?.message ?: "Unknown error"
                         binding.errorMessageView.visibility = View.VISIBLE
                         binding.animeContentList.visibility = View.GONE
                     } else {
                         // Data exists: Show Toast, ensure Content is visible
                         binding.errorMessageView.visibility = View.GONE
                         binding.animeContentList.visibility = View.VISIBLE
                         Toast.makeText(this, "${resource.error?.message}. Showing cached data.", Toast.LENGTH_SHORT).show()
                     }
                 }
            }
        }
    }
}