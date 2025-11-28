package com.example.proyectoaplicaciones.repository

import com.example.proyectoaplicaciones.data.model.Comentarios
import com.example.proyectoaplicaciones.data.model.Post
import com.example.proyectoaplicaciones.data.model.Rating
import com.example.proyectoaplicaciones.data.remote.ApiService
import com.example.proyectoaplicaciones.data.remote.RetrofitInstance

class PostRepository(private val apiService: ApiService = RetrofitInstance.api) {

    suspend fun getPosts(): List<Post> {
        return apiService.getPosts()
    }

    suspend fun getComments(postId: Int): List<Comentarios> {
        return apiService.getComments(postId)
    }

    suspend fun addPost(post: Post) {
        apiService.addPost(post)
    }

    suspend fun addComment(postId: Int, commentBody: String, author: String) {
        val newComment = Comentarios(id = 0, name = author, body = commentBody, postId = postId)
        apiService.addComment(postId, newComment)
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