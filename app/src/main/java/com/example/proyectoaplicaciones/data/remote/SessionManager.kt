package com.example.proyectoaplicaciones.data.remote

import com.example.proyectoaplicaciones.data.model.User

/**
 * Un objeto singleton para gestionar la sesión del usuario en memoria.
 * Esto permite que el estado de la sesión (quién es el usuario y su rol) persista
 * mientras la aplicación esté viva, incluso al cambiar de pantalla.
 */
object SessionManager {
    // --- INICIO DE LA CORRECCIÓN ---
    // La anotación @Volatile garantiza que los cambios en esta variable sean visibles
    // inmediatamente para todos los hilos, evitando condiciones de carrera y crashes.
    @Volatile
    var currentUser: User? = null
    // --- FIN DE LA CORRECCIÓN ---
}
