package com.example.proyectoaplicaciones.data.model

/**
 * Representa la respuesta del backend al hacer login o registrarse.
 * Este es el "diccionario" que le enseña a la app la estructura de la nueva respuesta.
 */
data class AuthResponse(
    val token: String,
    val id: Int,
    val username: String,
    val email: String,
    val role: Role
)
