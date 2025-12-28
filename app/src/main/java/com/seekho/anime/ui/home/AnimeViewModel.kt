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

class AnimeViewModel(private val repository: AnimeRepository) : ViewModel() {

    val animeList: LiveData<Resource<List<AnimeEntity>>> = repository.getTopAnime().asLiveData()

}

