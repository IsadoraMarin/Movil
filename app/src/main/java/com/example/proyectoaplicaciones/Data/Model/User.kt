package com.example.proyectoaplicaciones.Data.Model

enum class Role {
    USER,
    MODERATOR
}

data class User(
    val id: Int,
    val username: String,
    val role: Role,
    val token: String
)
