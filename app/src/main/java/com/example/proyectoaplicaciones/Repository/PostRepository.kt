package com.example.proyectoaplicaciones.Repository

import com.example.proyectoaplicaciones.Data.Model.Post
import com.example.proyectoaplicaciones.Data.Remote.RetroFitInstance

class PostRepository{
    suspend fun getPosts(): List<Post>{
        return RetroFitInstance.api.getPosts()
    }
}