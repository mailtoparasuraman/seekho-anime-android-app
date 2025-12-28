package com.seekho.anime.ui.compose

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.seekho.anime.ui.home.AnimeViewModel
import com.seekho.anime.ui.detail.AnimeInfoViewModel

@Composable
fun ComposeApp(
    homeViewModel: AnimeViewModel,
    detailViewModelFactory: (Int) -> AnimeInfoViewModel
) {
    val navController = rememberNavController()

    androidx.compose.material3.MaterialTheme {
        NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                viewModel = homeViewModel,
                onAnimeClick = { animeId ->
                    navController.navigate("detail/$animeId")
                }
            )
        }
        composable("detail/{animeId}") { backStackEntry ->
            val animeId = backStackEntry.arguments?.getString("animeId")?.toIntOrNull() ?: -1
            DetailScreen(
                viewModel = detailViewModelFactory(animeId),
                onBackClick = { navController.popBackStack() }
            )
        }
    }
    }
}
