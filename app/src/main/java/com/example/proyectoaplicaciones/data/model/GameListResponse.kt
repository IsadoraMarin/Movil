package com.example.proyectoaplicaciones.data.model

// Modelo para la respuesta de la lista de juegos de la API de RAWG
data class GameListResponse(
    val results: List<Game>
)