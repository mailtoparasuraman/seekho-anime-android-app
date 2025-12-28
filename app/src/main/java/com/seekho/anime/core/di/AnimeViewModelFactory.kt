package com.seekho.anime.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.seekho.anime.data.repository.AnimeRepository
import com.seekho.anime.ui.detail.AnimeInfoViewModel
import com.seekho.anime.ui.home.AnimeViewModel

class AnimeViewModelFactory(private val repository: AnimeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnimeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnimeViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(AnimeInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnimeInfoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
