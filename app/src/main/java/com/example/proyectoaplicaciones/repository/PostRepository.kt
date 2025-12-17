package com.example.proyectoaplicaciones.repository

import com.example.proyectoaplicaciones.data.model.Comentarios
import com.example.proyectoaplicaciones.data.model.Post
import com.example.proyectoaplicaciones.data.model.Rating
import com.example.proyectoaplicaciones.data.remote.ApiService
import com.example.proyectoaplicaciones.data.remote.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PostRepository(
    private val apiService: ApiService = RetrofitInstance.api
) {
    suspend fun getPosts(): List<Post> = withContext(Dispatchers.IO) {
        apiService.getPosts()
    }

    suspend fun getComments(postId: Int): List<Comentarios> = withContext(Dispatchers.IO) {
        apiService.getComments(postId)
    }

    suspend fun addPost(post: Post) = withContext(Dispatchers.IO) {
        apiService.addPost(post)
    }

    suspend fun addComment(postId: Int, commentBody: String, author: String) =
        withContext(Dispatchers.IO) {
            val newComment = Comentarios(
                id = 0,
                name = author,
                body = commentBody,
                postId = postId
            )
            apiService.addComment(postId, newComment)
        }

    suspend fun updatePost(postId: Int, post: Post) = withContext(Dispatchers.IO) {
        apiService.updatePost(postId, post)
    }

    suspend fun deletePost(postId: Int) = withContext(Dispatchers.IO) {
        apiService.deletePost(postId)
    }

    suspend fun deleteComment(commentId: Int) = withContext(Dispatchers.IO) {
        apiService.deleteComment(commentId)
    }

    suspend fun ratePost(postId: Int, value: Boolean) = withContext(Dispatchers.IO) {
        apiService.ratePost(postId, Rating(value))
    }
}