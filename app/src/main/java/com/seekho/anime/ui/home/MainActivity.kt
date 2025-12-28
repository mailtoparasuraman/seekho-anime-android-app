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

        binding.btnXml.setOnClickListener {
            binding.selectionLayout.visibility = View.GONE
            binding.xmlContent.visibility = View.VISIBLE
            setupXmlApp()
        }

        binding.btnCompose.setOnClickListener {
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
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    private fun setupXmlApp() {
        setupRecyclerView()
        observeViewModel()
        setupNetworkCallback()
    }
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    private fun setupNetworkCallback() {
        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
            
        connectivityManager.registerNetworkCallback(networkRequest, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                runOnUiThread {
                     if (animeAdapter.currentList.isEmpty()) {
                         Toast.makeText(this@MainActivity, "Syncing data...", Toast.LENGTH_SHORT).show()
                     }
                }
            }
        })
    }
    
    private fun setupRecyclerView() {
        animeAdapter = AnimeAdapter { anime ->
            openAnimeDetail(anime.mal_id)
        }

        binding.recyclerView.apply {
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
                     binding.progressBar.visibility = View.VISIBLE
                     resource.data?.let { animeAdapter.submitList(it) }
                 }
                 is com.seekho.anime.util.Resource.Success -> {
                     binding.progressBar.visibility = View.GONE
                     animeAdapter.submitList(resource.data)
                 }
                 is com.seekho.anime.util.Resource.Error -> {
                     binding.progressBar.visibility = View.GONE
                     resource.data?.let { animeAdapter.submitList(it) }
                     
                     // Check if data is present to enable "offline mode" visual or just error
                     if (animeAdapter.currentList.isEmpty()) {
                         Toast.makeText(this, resource.error?.message ?: "Unknown error", Toast.LENGTH_SHORT).show()
                     } else {
                         // Data is present, but an error occurred (e.g., refresh failed due to network)
                         Toast.makeText(this, "${resource.error?.message}. Showing cached data.", Toast.LENGTH_SHORT).show()
                     }
                 }
            }
        }
    }
}