package com.example.proyectoaplicaciones.data.model

import com.google.gson.annotations.SerializedName

// Modelo para un juego individual de la API de RAWG
data class Game(
    val id: Int,
    val slug: String,
    val name: String,
    @SerializedName("background_image")
    val background_image: String
)