package com.example.proyectoaplicaciones.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.proyectoaplicaciones.ui.screens.*
import com.example.proyectoaplicaciones.viewModel.AuthViewModel

sealed class Screen(val route: String, val label: String? = null, val icon: ImageVector? = null) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Register : Screen("register")
    object CreatePost : Screen("create_post")
    object PostDetail : Screen("post_detail")
    object Favorites : Screen("favorites")
    object EditProfile : Screen("edit_profile")

    object Popular : Screen("popular", "Populares", Icons.Default.Star)
    object News : Screen("news", "Noticias", Icons.Default.Info)
    object Games : Screen("games", "Juegos", Icons.Default.VideogameAsset)
    object Community : Screen("community", "Comunidad", Icons.Default.Edit)
    object Profile : Screen("profile", "Perfil", Icons.Default.Person)
}

val bottomNavItems = listOf(
    Screen.Popular,
    Screen.News,
    Screen.Games,
    Screen.Community,
    Screen.Profile
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Se crea una ÚNICA instancia del AuthViewModel aquí
    val authViewModel: AuthViewModel = viewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = bottomNavItems.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon!!, contentDescription = screen.label) },
                            label = { Text(screen.label!!) },
                            selected = navBackStackEntry?.destination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Welcome.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Se pasa la instancia compartida a cada pantalla que la necesita
            composable(Screen.Welcome.route) { WelcomeScreen(navController = navController, authViewModel = authViewModel) }
            composable(Screen.Login.route) { LoginScreen(navController = navController, authViewModel = authViewModel) }
            composable(Screen.Register.route) { RegisterScreen(navController = navController, authViewModel = authViewModel) }
            composable(Screen.Popular.route) { PopularScreen(navController = navController) }
            composable(Screen.News.route) { NewsScreen() }
            composable(Screen.Games.route) { GamesScreen() }
            composable(Screen.Community.route) { CommunityScreen(navController = navController, authViewModel = authViewModel) }
            composable(Screen.Profile.route) { ProfileScreen(navController = navController, authViewModel = authViewModel) }
            composable(Screen.CreatePost.route) { CreatePostScreen(navController = navController, authViewModel = authViewModel) }
            composable(Screen.PostDetail.route) { PostDetailScreen(navController = navController, authViewModel = authViewModel) }
            composable(Screen.Favorites.route) { FavoritesScreen(navController = navController) }
            composable(Screen.EditProfile.route) { EditProfileScreen(navController = navController, authViewModel = authViewModel) }
        }
    }
}
