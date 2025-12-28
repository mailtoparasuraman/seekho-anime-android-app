package com.seekho.anime.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seekho.anime.core.network.APIClient
import com.seekho.anime.data.model.AnimeDetail
import com.seekho.anime.core.database.AnimeEntity
import com.seekho.anime.util.Resource
import com.seekho.anime.data.repository.AnimeRepository
import kotlinx.coroutines.launch

class AnimeInfoViewModel(private val repository: AnimeRepository) : ViewModel() {

    private val _animeDetail = MutableLiveData<AnimeEntity>()
    val animeDetail: LiveData<AnimeEntity> = _animeDetail

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchAnimeDetail(animeId: Int) {
        viewModelScope.launch {
            _error.value = "Loading..." // Optional loading state
            when (val result = repository.getAnimeDetail(animeId)) {
                is Resource.Success -> {
                    result.data?.let { _animeDetail.value = it }
                }
                is Resource.Error -> {
                    _error.value = result.error?.message ?: "Error loading details"
                    result.data?.let { _animeDetail.value = it }
                }
                is Resource.Loading -> {
                    // Loading state if needed
                }
            }
        }
    }
}
