package com.example.proyectoaplicaciones.Repository

import com.example.proyectoaplicaciones.Data.Model.Post
import com.example.proyectoaplicaciones.Data.Remote.RetroFitInstance

class PostRepository{
    suspend fun getPostsByCategory(Categoria: String): List<Post>{
        return apiService.getPostsByCategory(Categoria)
    }

    suspend fun addPost(post: Post) {
        apiService.addPost(post)
    }

    suspend fun addComment(postId: Int, comment: String, author: String) {
        apiService.addComment(postId, Comment(comment, author))
    }
}