package com.example.proyectoaplicaciones.Data.Remote

import com.example.proyectoaplicaciones.Data.Model.ExternalDogResponse
import retrofit2.http.GET

interface ExternalApi{
    @GET("breeds/list/all")
    suspend fun getDogBreeds(): ExternalDogResponse

}