package com.example.proyectoaplicaciones.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.proyectoaplicaciones.ui.Screens.PopularScreen

sealed class Screen(val route: String){
    object Popular : Screen("Popular")
    object Noticias : Screen("Noticias")
    object Comunidad : Screen("Comunidad")
}

@Composable
fun appNavigation(navController: NavController){



}
