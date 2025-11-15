package com.example.proyectoaplicaciones.Data.Model

data class ExternalDogResponse(
    val message: Map<String, List<String>>,
    val status: String
)