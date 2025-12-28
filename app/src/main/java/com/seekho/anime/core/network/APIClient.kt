package com.seekho.anime.core.network

import com.seekho.anime.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object APIClient {

    private const val BASE_URL = BuildConfig.API_URL+"/v4/"

    val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }


    val httpClient by lazy { OkHttpClient.Builder().addInterceptor(interceptor).build()}


    val api: AnimeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
            .create(AnimeApiService::class.java)
    }
}
