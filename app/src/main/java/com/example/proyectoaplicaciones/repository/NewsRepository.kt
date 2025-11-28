package com.example.proyectoaplicaciones.repository

import com.example.proyectoaplicaciones.data.model.Article
import com.example.proyectoaplicaciones.data.remote.GNewsApiService
import com.example.proyectoaplicaciones.data.remote.GNewsRetrofitInstance

class NewsRepository(private val gnewsApi: GNewsApiService = GNewsRetrofitInstance.api) {

    private val apiKey = "0c52414453c2fdfeeadf53ae05b4ffed"

    suspend fun getGamingNews(): List<Article> {
        return try {
            val response = gnewsApi.getGamingNews(apiKey = apiKey)
            response.articles
        } catch (e: Exception) {
            // En caso de error, devuelve una lista vacía para no crashear la app.
            emptyList()
        }
    }
}