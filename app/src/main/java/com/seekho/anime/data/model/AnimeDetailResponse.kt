package com.seekho.anime.data.model

data class AnimeDetailResponse(
    val data: AnimeDetail
)

data class AnimeDetail(
    val mal_id: Int,
    val title: String,
    val synopsis: String?,
    val episodes: Int?,
    val score: Double?,
    val genres: List<Genre>,
    val images: Images,
    val trailer: Trailer?
)

//data class Genre(val name: String)
//
//data class Trailer(
//    val youtube_id: String?
//)
