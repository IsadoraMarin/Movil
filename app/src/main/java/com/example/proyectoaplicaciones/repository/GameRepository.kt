package com.example.proyectoaplicaciones.repository

import com.example.proyectoaplicaciones.data.model.Game
import com.example.proyectoaplicaciones.data.remote.ExternalApiService
import com.example.proyectoaplicaciones.data.remote.ExternalRetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GameRepository(
    private val externalApiService: ExternalApiService = ExternalRetrofitInstance.api
) {
    private val apiKey = "b2d276310a0a415eb7d52b922fcb3d3c"

    suspend fun getPopularGames(): List<Game> = withContext(Dispatchers.IO) {
        try {
            externalApiService.getPopularGames(apiKey = apiKey).results
        } catch (e: Exception) {
            // Relanzar excepción para que ViewModel la maneje
            throw e
        }
    }
}
