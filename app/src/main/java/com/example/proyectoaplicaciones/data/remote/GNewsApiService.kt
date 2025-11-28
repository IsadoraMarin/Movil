package com.example.proyectoaplicaciones.data.remote

import com.example.proyectoaplicaciones.data.model.GNewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GNewsApiService {
    @GET("search")
    suspend fun getGamingNews(
        @Query("apikey") apiKey: String,
        @Query("q") query: String = "videojuegos",
        @Query("lang") lang: String = "es",
        @Query("country") country: String = "cl"
    ): GNewsResponse
}
