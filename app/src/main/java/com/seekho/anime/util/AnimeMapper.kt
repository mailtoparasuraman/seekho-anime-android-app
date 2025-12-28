package com.seekho.anime.util

import com.seekho.anime.core.database.AnimeEntity
import com.seekho.anime.data.model.Anime

fun Anime.toEntity(): AnimeEntity {
    return AnimeEntity(
        mal_id = mal_id,
        title = title,
        imageUrl = images.jpg.large_image_url,
        score = score,
        episodes = episodes,
        synopsis = synopsis,
        genres = genres.joinToString(", ") { it.name },
        youtubeVideoId = trailer?.youtube_id
    )
}

fun com.seekho.anime.data.model.AnimeDetail.toEntity(): AnimeEntity {
    return AnimeEntity(
        mal_id = mal_id,
        title = title,
        imageUrl = images.jpg.large_image_url,
        score = score,
        episodes = episodes,
        synopsis = synopsis,
        genres = genres.joinToString(", ") { it.name },
        youtubeVideoId = trailer?.youtube_id
    )
}

fun List<com.seekho.anime.data.model.CharacterItem>.toCastEntityList(): List<com.seekho.anime.core.database.CastItemEntity> {
    return this.take(10).map { // Limit to top 10 cast members
        com.seekho.anime.core.database.CastItemEntity(
            name = it.character.name,
            role = it.role,
            imageUrl = it.character.images.jpg.image_url
        )
    }
}
