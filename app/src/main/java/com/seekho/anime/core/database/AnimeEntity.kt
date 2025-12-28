package com.seekho.anime.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "anime_table")
data class AnimeEntity(
    @PrimaryKey
    val mal_id: Int,
    val title: String,
    val imageUrl: String,
    val score: Double?,
    val episodes: Int?,
    val synopsis: String?,
    val genres: String, // Stored as comma-separated string for simplicity
    val youtubeVideoId: String?,
    val cast: List<CastItemEntity>? = null
)


