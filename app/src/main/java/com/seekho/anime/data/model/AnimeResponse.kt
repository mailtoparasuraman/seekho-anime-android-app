package com.seekho.anime.data.model

data class TopAnimeResponse(
    val pagination: Pagination,
    val data: List<Anime>
)

data class Pagination(
    val last_visible_page: Int,
    val has_next_page: Boolean,
    val current_page: Int,
    val items: PaginationItems
)
data class PaginationItems(
    val count: Int,
    val total: Int,
    val per_page: Int
)

data class Anime(
    val mal_id: Int,
    val title: String,
    val title_english: String?,
    val title_japanese: String?,
    val episodes: Int?,
    val score: Double?,
    val synopsis: String?,
    val rating: String?,
    val images: Images,
    val trailer: Trailer?,
    val genres: List<Genre>
)

data class Images(
    val jpg: Jpg
)

data class Jpg(
    val image_url: String,
    val small_image_url: String,
    val large_image_url: String
)


data class Trailer(
    val youtube_id: String?,
    val url: String?,
    val embed_url: String?
)

data class Genre(
    val mal_id: Int,
    val name: String
)

