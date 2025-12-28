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
            repository.getAnimeDetail(animeId).collect { result ->
                // Always update data if available (supports instant loading from cache)
                result.data?.let { _animeDetail.value = it }
                
                when (result) {
                    is Resource.Success -> {
                        // Data already updated above
                    }
                    is Resource.Error -> {
                         _error.value = result.error?.message ?: "Error loading details"
                         // Data already updated above with cached version if available
                    }
                    is Resource.Loading -> {
                        // We could show a loading indicator here if needed, 
                        // but since we update data immediately, the UI will populate.
                    }
                }
            }
        }
    }
}
