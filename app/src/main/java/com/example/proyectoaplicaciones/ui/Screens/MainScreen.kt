package com.example.proyectoaplicaciones.ui.Screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectoaplicaciones.Navigation.BottomNavBar
import com.example.proyectoaplicaciones.Navigation.Screen
import com.example.proyectoaplicaciones.ui.Screens.GamesScreen
import com.example.proyectoaplicaciones.viewmodel.AuthViewModel
import com.example.proyectoaplicaciones.viewmodel.PostViewModel

@Composable
fun MainScreen(mainNavController: NavController, authViewModel: AuthViewModel, postViewModel: PostViewModel) {
    val bottomBarNavController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar(navController = bottomBarNavController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(bottomBarNavController, startDestination = Screen.Popular.route) {
                composable(Screen.Popular.route) { PopularScreen(navController = mainNavController, viewModel = postViewModel, authViewModel = authViewModel) } 
                composable(Screen.Noticias.route) { NewsScreen(navController = mainNavController, viewModel = postViewModel, authViewModel = authViewModel) } 
                composable(Screen.Comunidad.route) { CommunityScreen(navController = mainNavController, viewModel = postViewModel, authViewModel = authViewModel) } 
                composable(Screen.Profile.route) { ProfileScreen(navController = mainNavController, authViewModel = authViewModel) } 
                composable(Screen.Games.route) { GamesScreen() }
            }
        }
    }
}
