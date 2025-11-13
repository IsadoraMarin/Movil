package com.example.proyectoaplicaciones.Navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.proyectoaplicaciones.viewmodel.AuthViewModel
import com.example.proyectoaplicaciones.viewmodel.PostViewModel
import com.example.proyectoaplicaciones.ui.screens.*

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
    object CreatePost : Screen("createPost")
    object PostDetail : Screen("postDetail")
    object Favorites : Screen("favorites")
}

@Composable
fun AppNavigation(navController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()
    val postViewModel: PostViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.Welcome.route) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(navController, authViewModel)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController, authViewModel)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController, authViewModel)
        }
        composable(Screen.Main.route) {
            MainScreen(mainNavController = navController, authViewModel = authViewModel, postViewModel = postViewModel)
        }
        
        composable(Screen.EditProfile.route) {
            EditProfileScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(Screen.CreatePost.route) {
            CreatePostScreen(navController = navController, postViewModel = postViewModel, authViewModel = authViewModel)
        }
        composable(Screen.PostDetail.route) {
            PostDetailScreen(navController = navController, postViewModel = postViewModel, authViewModel = authViewModel)
        }
        composable(Screen.Favorites.route) {
            FavoritesScreen(navController = navController, viewModel = postViewModel)
        }
    }
}
