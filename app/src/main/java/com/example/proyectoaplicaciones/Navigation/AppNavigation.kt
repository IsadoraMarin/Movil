package com.example.proyectoaplicaciones.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.proyectoaplicaciones.ViewModel.PostViewModel
import com.example.proyectoaplicaciones.ui.Screens.PopularScreen
import com.example.proyectoaplicaciones.ui.Screens.CommunityScreen
import com.example.proyectoaplicaciones.ui.Screens.NewsScreen


sealed class Screen(val route: String){
    object Popular : Screen("Popular")
    object Noticias : Screen("Noticias")
    object Comunidad : Screen("Comunidad")
}

@Composable
fun AppNavigation(navController: NavHostController, viewModel: PostViewModel) {
    NavHost(
        navController = navController,
        startDestination = Screen.Popular.route
    ) {
        composable(route = Screen.Popular.route) {
            PopularScreen(viewModel = viewModel)
        }
        composable(route = Screen.Noticias.route) {
            NewsScreen(viewModel = viewModel)
        }
        composable(route = Screen.Comunidad.route) {
            CommunityScreen(viewModel = viewModel)
        }
    }
}

