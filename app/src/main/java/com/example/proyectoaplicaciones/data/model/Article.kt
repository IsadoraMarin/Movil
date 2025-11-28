package com.example.proyectoaplicaciones.data.model

// Modelo para un solo artículo de noticia
data class Article(
    val title: String,
    val description: String,
    val url: String,
    val image: String,
    val publishedAt: String,
    val source: Source
)

// Modelo para la fuente del artículo, usado dentro de Article
data class Source(
    val name: String
)