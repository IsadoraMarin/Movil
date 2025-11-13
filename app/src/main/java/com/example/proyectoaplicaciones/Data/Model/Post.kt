package com.example.proyectoaplicaciones.Data.Model

/**
 * Define la estructura de una publicación.
 * @param score La puntuación total del post (likes - dislikes).
 */
data class Post(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String,
    var score: Int = 0 // El único contador de popularidad
)
