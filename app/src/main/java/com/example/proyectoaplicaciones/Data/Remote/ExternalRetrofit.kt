package com.example.proyectoaplicaciones.Data.Remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object ExternalRetrofit {
    val api: ExternalApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://dog.ceo/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExternalApi::class.java)
    }
}