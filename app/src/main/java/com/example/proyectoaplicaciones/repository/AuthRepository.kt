package com.example.proyectoaplicaciones.repository

import com.example.proyectoaplicaciones.data.model.AuthResponse
import com.example.proyectoaplicaciones.data.remote.ApiService
import com.example.proyectoaplicaciones.data.remote.LoginRequest
import com.example.proyectoaplicaciones.data.remote.RegisterRequest
import com.example.proyectoaplicaciones.data.remote.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class AuthRepository(
    private val apiService: ApiService = RetrofitInstance.api
) {
    // --- INICIO DE LA CORRECCIÓN ---
    // Se actualiza el tipo de retorno para que coincida con el ApiService.
    // Ahora el repositorio devuelve la respuesta completa del backend (token + usuario).
    suspend fun login(email: String, password: String): Response<AuthResponse> =
        withContext(Dispatchers.IO) {
            apiService.login(LoginRequest(email, password))
        }

    suspend fun register(email: String, password: String, username: String): Response<AuthResponse> =
        withContext(Dispatchers.IO) {
            apiService.register(RegisterRequest(email, password, username))
        }
    // --- FIN DE LA CORRECCIÓN ---
}
