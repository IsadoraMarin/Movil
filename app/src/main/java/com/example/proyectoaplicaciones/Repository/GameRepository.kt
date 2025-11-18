package com.example.proyectoaplicaciones.Repository

import com.example.proyectoaplicaciones.Data.Model.GameListResponse
import com.example.proyectoaplicaciones.Data.Remote.ExternalApiService

class GameRepository(private val externalApiService: ExternalApiService) {

    // Reemplaza "TU_API_KEY" con tu clave de API real de RAWG.
    private val apiKey = "b2d276310a0a415eb7d52b922fcb3d3c" //????????


    suspend fun getPopularGames(): GameListResponse {
        return externalApiService.getPopularGames(apiKey = apiKey)
    }
}