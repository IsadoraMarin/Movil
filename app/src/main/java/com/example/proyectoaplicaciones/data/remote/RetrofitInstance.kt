package com.example.proyectoaplicaciones.data.remote

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor // Se vuelve a importar
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .header("User-Agent", "TuForo-Android-App")
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")

        val token = SessionManager.currentUser?.token
        if (token != null) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}

object RetrofitInstance {
    private const val BASE_URL = "https://mi-api-server-benja.onrender.com/"

    // --- INICIO DE LA CORRECCIÓN DE DIAGNÓSTICO ---
    // Se vuelve a añadir el interceptor de logging para poder ver el cuerpo
    // de la petición en el Logcat y diagnosticar el Error 400.
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .addInterceptor(loggingInterceptor) // Se añade a la cadena de nuevo
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()
    // --- FIN DE LA CORRECCIÓN DE DIAGNÓSTICO ---

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}