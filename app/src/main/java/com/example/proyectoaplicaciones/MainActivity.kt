package com.example.proyectoaplicaciones

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.example.proyectoaplicaciones.Navigation.AppNavigation
import com.example.proyectoaplicaciones.ui.theme.ProyectoAplicacionesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ajuste visual para usar el contenido en pantalla completa (opcional)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        // Composable principal de la app
        setContent {
            ProyectoAplicacionesTheme {

                // Creamos el controlador de navegación
                val navController = rememberNavController()

                // Mostramos la navegación principal
                Surface {
                    AppNavigation(navController = navController)
                }
            }
        }
    }
}
