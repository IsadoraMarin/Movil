package com.example.proyectoaplicaciones.Data.Model

//Esto es para el tema de la validacion y que funcione en el backend
data class Post(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String,
    var score: Int = 0 // El único contador de popularidad
)
