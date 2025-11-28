package com.example.proyectoaplicaciones.data.model

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