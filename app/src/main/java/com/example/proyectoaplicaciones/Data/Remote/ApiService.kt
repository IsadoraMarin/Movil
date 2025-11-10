package com.example.proyectoaplicaciones.Data.Remote

import com.example.proyectoaplicaciones.Data.Model.Post
import retrofit2.http.GET

interface ApiService{
    @GET("/posts")
    suspend fun getPosts(): List<Post>
}
