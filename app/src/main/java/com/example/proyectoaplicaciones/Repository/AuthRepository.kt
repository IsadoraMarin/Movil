package com.example.proyectoaplicaciones.Repository

import com.example.proyectoaplicaciones.Data.Model.User
import com.example.proyectoaplicaciones.Data.Remote.ApiService
import com.example.proyectoaplicaciones.Data.Remote.LoginRequest
import com.example.proyectoaplicaciones.Data.Remote.RegisterRequest
import retrofit2.Response

class AuthRepository(private val apiService: ApiService) {

    suspend fun login(email: String, password: String): Response<User> {
        return apiService.login(LoginRequest(email, password))
    }

    suspend fun register(email: String, password: String, username: String): Response<User> {
        return apiService.register(RegisterRequest(email, password, username))
    }
}