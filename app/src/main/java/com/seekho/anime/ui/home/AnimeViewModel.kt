package com.seekho.anime.ui.home


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.seekho.anime.core.database.AnimeEntity
import com.seekho.anime.data.repository.AnimeRepository
import com.seekho.anime.util.Resource
import kotlinx.coroutines.launch

import androidx.lifecycle.switchMap

class AnimeViewModel(private val repository: AnimeRepository) : ViewModel() {

    private val refreshTrigger = MutableLiveData(Unit)
    
    val animeList: LiveData<Resource<List<AnimeEntity>>> = refreshTrigger.switchMap {
        repository.getTopAnime().asLiveData()
    }
    
    fun refresh() {
        refreshTrigger.value = Unit
    }

}

