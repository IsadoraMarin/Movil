package com.example.proyectoaplicaciones.repository

import com.example.proyectoaplicaciones.data.model.Article
import com.example.proyectoaplicaciones.data.remote.GNewsApiService
import com.example.proyectoaplicaciones.data.remote.GNewsRetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NewsRepository(
    private val gnewsApi: GNewsApiService = GNewsRetrofitInstance.api
) {
    private val apiKey = "0c52414453c2fdfeeadf53ae05b4ffed"

    suspend fun getGamingNews(): List<Article> = withContext(Dispatchers.IO) {
        try {
            val response = gnewsApi.getGamingNews(apiKey = apiKey)
            response.articles
        } catch (e: Exception) {
            // Relanzar excepción para que ViewModel la maneje
            throw e
        }
    }
}
