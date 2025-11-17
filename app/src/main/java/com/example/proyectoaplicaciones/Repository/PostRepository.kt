package com.example.proyectoaplicaciones.Repository

import com.example.proyectoaplicaciones.Data.Model.Comentarios
import com.example.proyectoaplicaciones.Data.Model.Post
import com.example.proyectoaplicaciones.Data.Model.Rating
import com.example.proyectoaplicaciones.Data.Remote.RetroFitInstance

class PostRepository {
    private val apiService = RetroFitInstance.api


    suspend fun getPosts(): List<Post> {
        return apiService.getPosts()
    }

    suspend fun addPost(post: Post) {
        apiService.addPost(post)
    }

    suspend fun addComment(postId: Int, comment: String, author: String) {
        apiService.addComment(postId, Comentarios(0, author, "", comment))
    }

    suspend fun updatePost(postId: Int, post: Post) {
        apiService.updatePost(postId, post)
    }

    suspend fun deletePost(postId: Int) {
        apiService.deletePost(postId)
    }

    suspend fun ratePost(postId: Int, value: Boolean) {
        apiService.ratePost(postId, Rating(value))
    }
}
