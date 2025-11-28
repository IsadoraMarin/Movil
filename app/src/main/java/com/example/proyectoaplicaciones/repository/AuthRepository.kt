package com.example.proyectoaplicaciones.repository

import com.example.proyectoaplicaciones.data.model.User
import com.example.proyectoaplicaciones.data.remote.ApiService
import com.example.proyectoaplicaciones.data.remote.LoginRequest
import com.example.proyectoaplicaciones.data.remote.RegisterRequest
import com.example.proyectoaplicaciones.data.remote.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class AuthRepository(private val apiService: ApiService = RetrofitInstance.api) {
    // Se envuelven las llamadas de red en withContext(Dispatchers.IO) para asegurar que se ejecuten en un hilo de fondo.
    // Esto previene los errores ANR (Application Not Responding).
    suspend fun login(email: String, password: String): Response<User> = withContext(Dispatchers.IO) {
        apiService.login(LoginRequest(email, password))
    }

    suspend fun register(email: String, password: String, username: String): Response<User> = withContext(Dispatchers.IO) {
        apiService.register(RegisterRequest(email, password, username))
    }
}
