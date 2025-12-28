package com.seekho.anime.data.model

data class CharacterResponse(
    val data: List<CharacterItem>
)

data class CharacterItem(
    val character: CharacterInfo,
    val role: String,
    val favorites: Int
)

data class CharacterInfo(
    val mal_id: Int,
    val url: String,
    val images: CharacterImages,
    val name: String
)

data class CharacterImages(
    val jpg: Jpg
)
