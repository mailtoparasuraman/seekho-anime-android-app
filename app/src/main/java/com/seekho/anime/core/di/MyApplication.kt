package com.seekho.anime.core.di

import android.app.Application
import com.seekho.anime.core.database.AppDatabase
import com.seekho.anime.data.repository.AnimeRepository
import com.seekho.anime.core.network.APIClient

class MyApplication : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { AnimeRepository(database, APIClient.api) }
    
    override fun onCreate() {
        super.onCreate()
    }
}