package com.example.proyectoaplicaciones

import android.os.Bundle
import com.example.proyectoaplicaciones.ui.Screens.PopularScreen
import androidx.core.view.WindowCompat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectoaplicaciones.ui.theme.ProyectoAplicacionesTheme
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        enableEdgeToEdge()
        setContent {
            ProyectoAplicacionesTheme {
                val postViewModel: com.example.proyectoaplicaciones.ViewModel.PostViewModel = viewModel()
                PopularScreen(viewModel = postViewModel)
                }
            }
        }
    }