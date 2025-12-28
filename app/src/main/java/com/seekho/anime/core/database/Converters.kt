package com.seekho.anime.core.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromCastList(value: List<CastItemEntity>?): String? {
        if (value == null) return null
        val gson = Gson()
        val type = object : TypeToken<List<CastItemEntity>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toCastList(value: String?): List<CastItemEntity>? {
        if (value == null) return null
        val gson = Gson()
        val type = object : TypeToken<List<CastItemEntity>>() {}.type
        return gson.fromJson(value, type)
    }
}
