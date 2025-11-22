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


        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()


        setContent {
            ProyectoAplicacionesTheme {


                val navController = rememberNavController()


                Surface {
                    AppNavigation(navController = navController)
                }
            }
        }
    }
}
