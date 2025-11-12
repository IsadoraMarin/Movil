package com.example.proyectoaplicaciones.Navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.proyectoaplicaciones.ViewModel.AuthViewModel
import com.example.proyectoaplicaciones.ui.screens.*

// Sealed class para las rutas de la aplicación
sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Register : Screen("register")
    object Main : Screen("main") 
    object Popular : Screen("popular")
    object Noticias : Screen("noticias")
    object Comunidad : Screen("comunidad")
    object Profile : Screen("profile")
    object EditProfile : Screen("editProfile") 
    object CreatePost : Screen("createPost") // Nueva ruta para crear posts
}

// Composable principal que define la navegación
@Composable
fun AppNavigation(navController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.Welcome.route) {
        // Flujo de Autenticación
        composable(Screen.Welcome.route) {
            WelcomeScreen(navController, authViewModel)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController, authViewModel)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController, authViewModel)
        }

        // Contenido Principal de la App
        composable(Screen.Main.route) {
            MainScreen(mainNavController = navController, authViewModel = authViewModel)
        }
        
        // Pantallas de "detalle" que se muestran por encima de la navegación principal
        composable(Screen.EditProfile.route) {
            EditProfileScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(Screen.CreatePost.route) {
            CreatePostScreen(navController = navController)
        }
    }
}
