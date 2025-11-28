package com.example.proyectoaplicaciones.data.remote

// Modelo para el cuerpo de la petición de registro
data class RegisterRequest(
    val email: String,
    val password: String,
    val username: String
)