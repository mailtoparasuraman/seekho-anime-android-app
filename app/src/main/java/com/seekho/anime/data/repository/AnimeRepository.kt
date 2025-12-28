package com.seekho.anime.data.repository

import androidx.room.withTransaction
import com.seekho.anime.core.network.AnimeApiService
import com.seekho.anime.core.database.AnimeEntity
import com.seekho.anime.core.database.AppDatabase
import com.seekho.anime.util.toEntity
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
                animeDao.clearAll()
                val entities = response.data.map { it.toEntity() }
                animeDao.insertAll(entities)
            }
        }
    )
    
    suspend fun getAnimeDetail(id: Int): Resource<AnimeEntity> {
        val localAnime = animeDao.getAnimeById(id)
        if (localAnime != null) {
            return Resource.Success(localAnime)
        }

        return try {
             val response = api.getAnimeDetails(id)
             val charactersResponse = api.getAnimeCharacters(id)
             
             val entity = response.data.toEntity().copy(
                 cast = charactersResponse.data.toCastEntityList()
             )
             
             db.withTransaction {
                 animeDao.insertAll(listOf(entity))
             }
             Resource.Success(entity)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}
