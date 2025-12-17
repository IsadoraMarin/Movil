package com.example.proyectoaplicaciones.data.model

/**
 * Modelo para el usuario.
 */
data class User(
    val id: Int,
    val username: String,
    val email: String,
    val role: Role? = null, // Hacemos el rol nullable también por seguridad

    // --- INICIO DE LA CORRECCIÓN ---
    // Se hace el token nullable (String?) porque el backend actual no lo está enviando.
    // Esto evita el crash de deserialización de Gson al iniciar sesión.
    val token: String? = null
    // --- FIN DE LA CORRECCIÓN ---
)
