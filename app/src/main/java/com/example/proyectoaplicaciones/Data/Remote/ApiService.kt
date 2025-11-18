package com.example.proyectoaplicaciones.Data.Remote

import com.example.proyectoaplicaciones.Data.Model.Comentarios
import com.example.proyectoaplicaciones.Data.Model.Post
import com.example.proyectoaplicaciones.Data.Model.Rating
import com.example.proyectoaplicaciones.Data.Model.User
import retrofit2.Response
import retrofit2.http.*

// Data classes for auth requests
data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val email: String, val password: String, val username: String)

interface ApiService {

    // Auth endpoints
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<User>

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<User>

    // Post endpoints
    // Obtiene TODOS los posts. Ya no se filtra por categoría aquí.
    @GET("posts")
    suspend fun getPosts(): List<Post>

    @POST("posts")
    suspend fun addPost(@Body post: Post)

    @POST("posts/{id}/comments")
    suspend fun addComment(@Path("id") postId: Int, @Body comentarios: Comentarios)

    @PUT("posts/{id}")
    suspend fun updatePost(@Path("id") postId: Int, @Body post: Post)

    @DELETE("posts/{id}")
    suspend fun deletePost(@Path("id") postId: Int)

    @POST("posts/{id}/rate")
    suspend fun ratePost(@Path("id") postId: Int, @Body rating: Rating)
}