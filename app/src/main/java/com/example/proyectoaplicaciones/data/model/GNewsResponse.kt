package com.example.proyectoaplicaciones.data.model

/**
 * Representa la respuesta completa de la API de GNews.
 * Este archivo unifica la definición que estaba duplicada.
 */
data class GNewsResponse(
    val totalArticles: Int,
    val articles: List<Article>
)