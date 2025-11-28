package com.example.proyectoaplicaciones.data.model

/**
 * Representa la respuesta completa de la API de GNews.
 */
data class GNewsResponse(    val totalArticles: Int,
                             val articles: List<Article>
)
