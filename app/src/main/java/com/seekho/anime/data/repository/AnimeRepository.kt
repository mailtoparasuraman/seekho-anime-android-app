package com.seekho.anime.data.repository

import androidx.room.withTransaction
import com.seekho.anime.core.network.AnimeApiService
import com.seekho.anime.core.database.AnimeEntity
import com.seekho.anime.core.database.AppDatabase
import com.seekho.anime.util.toEntity
import com.seekho.anime.util.toCastEntityList
import com.seekho.anime.util.Resource
import com.seekho.anime.util.networkBoundResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

class AnimeRepository(
    private val db: AppDatabase,
    private val api: AnimeApiService
) {
    private val animeDao = db.animeDao()

    fun getTopAnime(): Flow<Resource<List<AnimeEntity>>> = networkBoundResource(
        query = {
            animeDao.getAllAnime()
        },
        fetch = {
            delay(1000) 
            api.getTopAnime()
        },
        saveFetchResult = { response ->
            db.withTransaction {
                // Fetch existing data to preserve 'cast' information
                val oldList = animeDao.getAllAnimeSync()
                val oldMap = oldList.associateBy { it.mal_id }
                
                val newEntities = response.data.map { apiModel ->
                    val newEntity = apiModel.toEntity()
                    // If we already have this anime in cache, check if it has cast info.
                    // If so, preserve it!
                    val oldEntity = oldMap[newEntity.mal_id]
                    if (oldEntity?.cast != null) {
                        newEntity.copy(cast = oldEntity.cast)
                    } else {
                        newEntity
                    }
                }
                
                animeDao.clearAll()
                animeDao.insertAll(newEntities)
            }
        }
    )
    
    fun getAnimeDetail(id: Int): Flow<Resource<AnimeEntity>> = kotlinx.coroutines.flow.flow {
        val localAnime = animeDao.getAnimeById(id)
        
        // Emit local data immediately as Loading (so UI shows up fast)
        emit(Resource.Loading(localAnime))
        
        // If we have local data AND the cast info, we are good to go. 
        // We can emit Success and stop here.
        if (localAnime != null && localAnime.cast != null) {
            emit(Resource.Success(localAnime))
            return@flow
        }

        try {
             val response = api.getAnimeDetails(id)
             val charactersResponse = api.getAnimeCharacters(id)
             
             val entity = response.data.toEntity().copy(
                 cast = charactersResponse.data.toCastEntityList()
             )
             
             db.withTransaction {
                 animeDao.insertAll(listOf(entity))
             }
             emit(Resource.Success(entity))
        } catch (e: Exception) {
            val error = when {
                e is retrofit2.HttpException && e.code() == 429 -> {
                    Exception("Rate limit exceeded. Please wait a moment.")
                }
                e is java.io.IOException -> {
                    Exception("No internet connection. Please check your network.")
                }
                else -> e
            }
            emit(Resource.Error(error, localAnime))
        }
    }
}
