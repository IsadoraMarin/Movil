package com.example.proyectoaplicaciones.data.remote

import com.example.proyectoaplicaciones.data.model.Article

// Representa la respuesta completa de la API de GNews.
data class GNewsResponse(
    val totalArticles: Int,
    val articles: List<Article>
)
