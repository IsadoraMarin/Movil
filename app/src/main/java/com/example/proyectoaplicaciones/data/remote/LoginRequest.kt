package com.example.proyectoaplicaciones.data.remote

// Modelo para el cuerpo de la petición de login
data class LoginRequest(
    val email: String,
    val password: String
)