package com.example.proyectoaplicaciones.Repository

import com.example.proyectoaplicaciones.Data.Remote.ExternalApi

class ExternalRepository(
    private val api: ExternalApi
){
    suspend fun fetchBreeds(): List<String>{
        val response = api.getDogBreeds()
        return response.message.keys.toList()
    }
}