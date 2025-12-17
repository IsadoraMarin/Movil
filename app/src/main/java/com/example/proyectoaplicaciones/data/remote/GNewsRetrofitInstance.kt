package com.example.proyectoaplicaciones.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object GNewsRetrofitInstance {
    private const val BASE_URL = "https://gnews.io/api/v4/"

    // --- INICIO DE LA CORRECCIÓN ---
    // Se elimina el HttpLoggingInterceptor para evitar errores de "La aplicación no responde" (ANR).
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()
    // --- FIN DE LA CORRECCIÓN ---

    val api: GNewsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GNewsApiService::class.java)
    }
}
