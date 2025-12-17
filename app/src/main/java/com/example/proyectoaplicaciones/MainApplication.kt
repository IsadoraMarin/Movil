package com.example.proyectoaplicaciones

import android.app.Application
import com.example.proyectoaplicaciones.data.remote.ExternalRetrofitInstance
import com.example.proyectoaplicaciones.data.remote.GNewsRetrofitInstance
import com.example.proyectoaplicaciones.data.remote.RetrofitInstance
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Clase de Aplicación personalizada para realizar inicializaciones al inicio de la app.
 */
@OptIn(DelicateCoroutinesApi::class)
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Pre-calentar las instancias de Retrofit en un hilo de fondo
        // para evitar que la inicialización perezosa ("by lazy") bloquee el hilo principal.
        GlobalScope.launch {
            // Simplemente acceder a cada instancia fuerza su inicialización en este hilo de fondo.
            RetrofitInstance.api
            ExternalRetrofitInstance.api
            GNewsRetrofitInstance.api
        }
    }
}
