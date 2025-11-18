package com.example.proyectoaplicaciones.Data.Model

import com.google.gson.annotations.SerializedName

// Modelo para la respuesta principal de la API de RAWG
data class GameListResponse(
    val results: List<ExternalGame>
)

// Modelo que representa un solo juego de la API externa
data class ExternalGame(
    val id: Int,
    val name: String,
    val released: String?,
    @SerializedName("background_image") val backgroundImage: String?,
    val rating: Double
)
