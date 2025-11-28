package com.example.proyectoaplicaciones.Data.Model

import com.google.gson.annotations.SerializedName

// Modelo para la respuesta principal de la API de RAWG
data class GameListResponse(
    // Ahora espera una lista de "Game", el nombre correcto.
    val results: List<Game>
)

// CORRECCIÓN: Se renombra la clase a "Game" para que coincida con el resto del código.
data class Game(
    val id: Int,
    val name: String,
    val released: String?,
    @SerializedName("background_image") val backgroundImage: String?,
    val rating: Double
)
