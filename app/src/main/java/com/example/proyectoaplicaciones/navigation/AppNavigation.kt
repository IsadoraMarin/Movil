package com.example.proyectoaplicaciones.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
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
import com.example.proyectoaplicaciones.ui.screens.CommunityScreen
import com.example.proyectoaplicaciones.ui.screens.CreatePostScreen
import com.example.proyectoaplicaciones.ui.screens.EditProfileScreen
import com.example.proyectoaplicaciones.ui.screens.FavoritesScreen
import com.example.proyectoaplicaciones.ui.screens.GamesScreen
import com.example.proyectoaplicaciones.ui.screens.LoginScreen
import com.example.proyectoaplicaciones.ui.screens.NewsScreen
import com.example.proyectoaplicaciones.ui.screens.PopularScreen
import com.example.proyectoaplicaciones.ui.screens.PostDetailScreen
import com.example.proyectoaplicaciones.ui.screens.ProfileScreen
import com.example.proyectoaplicaciones.ui.screens.RegisterScreen
import com.example.proyectoaplicaciones.ui.screens.WelcomeScreen
import com.example.proyectoaplicaciones.viewModel.AuthViewModel
import com.example.proyectoaplicaciones.viewModel.GameViewModel
import com.example.proyectoaplicaciones.viewModel.NewsViewModel
import com.example.proyectoaplicaciones.viewModel.PostViewModel

// --- INICIO DE LA CORRECCIÓN ---
// 1. Data class para los items de la barra de navegación
data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

// 2. Lista con los items de la barra de navegación
val navItems = listOf(
    NavItem(label = "Popular", icon = Icons.Filled.Whatshot, route = Screen.Popular.route),
    NavItem(label = "Comunidad", icon = Icons.Filled.Language, route = Screen.Community.route),
    NavItem(label = "Noticias", icon = Icons.Filled.Newspaper, route = Screen.News.route),
    NavItem(label = "Juegos", icon = Icons.Filled.Games, route = Screen.Games.route),
    NavItem(label = "Perfil", icon = Icons.Filled.AccountCircle, route = Screen.Profile.route),
)
// --- FIN DE LA CORRECCIÓN ---

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val authViewModel: AuthViewModel = viewModel()
    val postViewModel: PostViewModel = viewModel()
    val newsViewModel: NewsViewModel = viewModel()
    val gameViewModel: GameViewModel = viewModel()

    // --- INICIO DE LA CORRECCIÓN ---
    // 3. Obtenemos la ruta actual para saber qué pantallas no deben mostrar la barra
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    val screensWithoutBottomBar = setOf(
        Screen.Welcome.route,
        Screen.Login.route,
        Screen.Register.route
    )

    // 4. Usamos Scaffold para darle una estructura a la app (con barra de navegación)
    Scaffold(
        bottomBar = {
            // Mostrar la barra solo si la ruta actual no está en la lista de exclusión
            if (currentRoute !in screensWithoutBottomBar) {
                BottomAppBar {
                    navItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                navController.navigate(item.route) {
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
    ) { innerPadding: PaddingValues -> // El NavHost va dentro del contenido del Scaffold
        NavHost(
            navController = navController, 
            startDestination = Screen.Welcome.route,
            modifier = Modifier.padding(innerPadding) // Aplicar el padding del Scaffold
        ) {
    // --- FIN DE LA CORRECCIÓN ---
            composable(Screen.Welcome.route) {
                WelcomeScreen(navController, authViewModel)
            }
            composable(Screen.Login.route) {
                LoginScreen(navController, authViewModel)
            }
            composable(Screen.Register.route) {
                RegisterScreen(navController, authViewModel)
            }
            composable(Screen.Popular.route) {
                PopularScreen(navController, postViewModel)
            }
            composable(Screen.Community.route) {
                CommunityScreen(navController, authViewModel, postViewModel)
            }
            composable(Screen.News.route) {
                NewsScreen(newsViewModel)
            }
            composable(Screen.Games.route) {
                GamesScreen(gameViewModel)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(navController, authViewModel)
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen(navController, postViewModel)
            }
            composable(Screen.PostDetail.route) {
                PostDetailScreen(navController, authViewModel, postViewModel)
            }
            composable(Screen.CreatePost.route) {
                CreatePostScreen(navController, authViewModel, postViewModel)
            }
            composable(Screen.EditProfile.route) {
                EditProfileScreen(navController, authViewModel)
            }
        }
    }
}