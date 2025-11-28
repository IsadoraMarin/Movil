package com.example.proyectoaplicaciones.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RawgApiInstance {
    private const val BASE_URL = "https://api.rawg.io/api/"

    val api: ExternalApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExternalApiService::class.java)
    }
}