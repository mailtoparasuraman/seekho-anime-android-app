package com.seekho.anime.core.network

import com.seekho.anime.data.model.AnimeDetailResponse
import com.seekho.anime.data.model.TopAnimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface AnimeApiService {

    @GET("top/anime")
    suspend fun getTopAnime(): TopAnimeResponse

    @GET("anime/{id}")
    suspend fun getAnimeDetails(
        @Path("id") animeId: Int
    ): AnimeDetailResponse
    @GET("anime/{id}/characters")
    suspend fun getAnimeCharacters(
        @Path("id") animeId: Int
    ): CharacterResponse
}
