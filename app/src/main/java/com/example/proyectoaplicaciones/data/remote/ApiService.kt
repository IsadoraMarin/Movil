package com.example.proyectoaplicaciones.data.remote

import com.example.proyectoaplicaciones.data.model.AuthResponse
import com.example.proyectoaplicaciones.data.model.Comentarios
import com.example.proyectoaplicaciones.data.model.Post
import com.example.proyectoaplicaciones.data.model.Rating
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // --- INICIO DE LA CORRECCIÓN ---
    // Se actualiza la respuesta esperada a AuthResponse, que contiene el token y los datos del usuario.
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
    // --- FIN DE LA CORRECCIÓN ---

    @GET("api/posts")
    suspend fun getPosts(): List<Post>

    @GET("api/posts/{id}/comments")
    suspend fun getComments(@Path("id") postId: Int): List<Comentarios>

    @POST("api/posts")
    suspend fun addPost(@Body post: Post): Response<Void>

    @POST("api/posts/{id}/comments")
    suspend fun addComment(@Path("id") postId: Int, @Body comment: Comentarios): Response<Void>

    @PUT("api/posts/{id}")
    suspend fun updatePost(@Path("id") postId: Int, @Body post: Post): Response<Void>

    @DELETE("api/posts/{id}")
    suspend fun deletePost(@Path("id") postId: Int): Response<Void>

    @DELETE("api/comments/{id}")
    suspend fun deleteComment(@Path("id") commentId: Int): Response<Void>

    @POST("api/posts/{id}/rate")
    suspend fun ratePost(@Path("id") postId: Int, @Body value: Rating): Response<Void>
}
