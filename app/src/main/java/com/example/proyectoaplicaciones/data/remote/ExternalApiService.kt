package com.example.proyectoaplicaciones.data.remote

import com.example.proyectoaplicaciones.data.model.GameListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ExternalApiService {
    @GET("games")
    suspend fun getPopularGames(
        @Query("key") apiKey: String,
        @Query("page_size") pageSize: Int = 20
    ): GameListResponse
}