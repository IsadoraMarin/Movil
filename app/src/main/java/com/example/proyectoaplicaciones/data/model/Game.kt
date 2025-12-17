package com.example.proyectoaplicaciones.data.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo para un juego individual de la API de RAWG.
 */
data class Game(
    val id: Int,
    val slug: String = "",
    val name: String,
    val released: String? = null,
    
    // --- CORRECCIÓN ---
    // Se elimina el campo duplicado "backgroundImage". 
    // Gson no sabía a qué campo asignar el valor de "background_image" del JSON.
    @SerializedName("background_image")
    val background_image: String? = null,
    // --- FIN DE LA CORRECCIÓN ---
    
    val rating: Double = 0.0
) {
    // Esta propiedad sigue funcionando para obtener la URL de la imagen
    val imageUrl: String?
        get() = background_image
}
