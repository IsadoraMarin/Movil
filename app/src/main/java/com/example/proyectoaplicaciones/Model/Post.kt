package com.example.proyectoaplicaciones.Model

data class PostData(
    val id: Int,
    val title: String,
    val content: String,
    val categoria: String,
    var likes: Int = 0,
    var comentarios: Int = 0,
    val autor: String,
    val date: String
)