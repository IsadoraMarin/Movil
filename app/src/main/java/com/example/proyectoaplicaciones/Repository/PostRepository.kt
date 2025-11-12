package com.example.proyectoaplicaciones.Repository

import com.example.proyectoaplicaciones.Data.Model.Comentarios
import com.example.proyectoaplicaciones.Data.Model.Post
import com.example.proyectoaplicaciones.Data.Remote.RetroFitInstance

class PostRepository{
    private val apiService = RetroFitInstance.api

    suspend fun getPostsByCategory(categoria: String): List<Post>{
        return apiService.getPostsByCategory(categoria)
    }

    suspend fun addPost(post: Post) {
        apiService.addPost(post)
    }

    suspend fun addComment(postId: Int, comment: String, author: String) {
        val newComment = Comentarios(id = 0, name = author, email = "temp@gmail.com", body = comment)
        apiService.addComment(postId, newComment)
    }
}
