package com.seekho.anime.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.seekho.anime.core.database.AnimeEntity
import com.seekho.anime.ui.home.AnimeViewModel
import com.seekho.anime.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AnimeViewModel,
    onAnimeClick: (Int) -> Unit
) {
    val animeState by viewModel.animeList.observeAsState(Resource.Loading())

    val snackbarHostState = remember { SnackbarHostState() }
    
    // Show snackbar on error if data is present
    LaunchedEffect(animeState) {
        if (animeState is Resource.Error) {
             snackbarHostState.showSnackbar(
                 message = animeState.error?.message ?: "Unknown Error"
             )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Anime Series (Compose)") })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            val resource = animeState
            // Show list if data exists (Success or Error with cached data)
            if (resource.data != null && resource.data.isNotEmpty()) {
                LazyColumn {
                    items(resource.data) { anime ->
                        AnimeItem(anime, onAnimeClick)
                    }
                }
            } else if (resource is Resource.Loading) {
                 CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (resource is Resource.Error) {
                 // Error and no data
                 Column(
                     modifier = Modifier.align(Alignment.Center),
                     horizontalAlignment = Alignment.CenterHorizontally
                 ) {
                     Text(
                        text = resource.error?.message ?: "Unknown Error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                     Button(onClick = { /* Retry logic could be added here */ }) {
                         Text("Retry")
                     }
                 }
            }
            
            // Show loading indicator on top if refreshing
            if (resource is Resource.Loading && resource.data != null) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter))
            }
        }
    }
}

@Composable
fun AnimeItem(anime: AnimeEntity, onClick: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick(anime.mal_id) },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = anime.imageUrl,
                contentDescription = null,
                modifier = Modifier.size(80.dp, 120.dp),
                contentScale = ContentScale.Crop,
                placeholder = androidx.compose.ui.res.painterResource(com.seekho.anime.R.drawable.ic_launcher_background),
                error = androidx.compose.ui.res.painterResource(com.seekho.anime.R.drawable.ic_launcher_background)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = anime.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = "Episodes: ${anime.episodes ?: "?"}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Rating: ${anime.score ?: "N/A"}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
