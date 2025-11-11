package com.example.proyectoaplicaciones.Data.Remote

import com.example.proyectoaplicaciones.Data.Model.Comentarios
import com.example.proyectoaplicaciones.Data.Model.Post
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService{
    @GET("posts/{category}")
    suspend fun getPostsByCategory(@Path("category") category: String): List<Post>

    @POST("posts")
    suspend fun addPost(@Body post: Post)

    @POST("posts/{id}/comments")
    suspend fun addComment(@Path("id") postId: Int, @Body comentarios: Comentarios)
}
