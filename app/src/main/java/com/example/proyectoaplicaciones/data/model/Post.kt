package com.example.proyectoaplicaciones.data.model

// Modelo para una publicación (post)
data class Post(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String,
    val score: Int = 0 // <-- CORREGIDO: de 'var' a 'val'
)