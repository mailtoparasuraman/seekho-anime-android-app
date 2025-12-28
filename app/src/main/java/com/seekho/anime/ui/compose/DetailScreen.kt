package com.seekho.anime.ui.compose

import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.seekho.anime.core.database.AnimeEntity
import com.seekho.anime.core.database.CastItemEntity
import com.seekho.anime.ui.detail.AnimeInfoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: AnimeInfoViewModel,
    onBackClick: () -> Unit
) {
    val anime by viewModel.animeDetail.observeAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(anime?.title ?: "Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        anime?.let { detail ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {
                if (detail.youtubeVideoId != null) {
                    TrailerView(videoId = detail.youtubeVideoId)
                } else {
                    AsyncImage(
                        model = detail.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop,
                        placeholder = androidx.compose.ui.res.painterResource(com.seekho.anime.R.drawable.ic_launcher_background),
                        error = androidx.compose.ui.res.painterResource(com.seekho.anime.R.drawable.ic_launcher_background)
                    )
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = detail.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Rating: ${detail.score ?: "N/A"}", color = MaterialTheme.colorScheme.primary)
                    Text(text = "Episodes: ${detail.episodes ?: "N/A"}", color = MaterialTheme.colorScheme.secondary)
                    Text(text = detail.genres, style = MaterialTheme.typography.bodyMedium, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Synopsis", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = detail.synopsis ?: "No synopsis", style = MaterialTheme.typography.bodyMedium)
                    
                    if (!detail.cast.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Main Cast", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        CastList(detail.cast)
                    }
                }
            }
        }
    }
}

@Composable
fun TrailerView(videoId: String) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webChromeClient = WebChromeClient()
                loadUrl("https://www.youtube.com/embed/$videoId")
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    )
}

@Composable
fun CastList(cast: List<CastItemEntity>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(cast) { item ->
            Column(modifier = Modifier.width(100.dp)) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier
                        .size(100.dp, 120.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Crop,
                    placeholder = androidx.compose.ui.res.painterResource(com.seekho.anime.R.drawable.ic_launcher_background),
                    error = androidx.compose.ui.res.painterResource(com.seekho.anime.R.drawable.ic_launcher_background)
                )
                Text(text = item.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(text = item.role, style = MaterialTheme.typography.labelSmall, maxLines = 1)
            }
        }
    }
}
