package com.example.proyectoaplicaciones.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons 
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.proyectoaplicaciones.Navigation.Screen
import com.example.proyectoaplicaciones.ViewModel.AuthViewModel
import com.example.proyectoaplicaciones.ViewModel.PostViewModel

private val mainScreens = listOf(
    Screen.Popular,
    Screen.Noticias,
    Screen.Comunidad,
    Screen.Profile
)

@Composable
fun MainScreen(mainNavController: NavController, authViewModel: AuthViewModel) {
    val bottomBarNavController = rememberNavController()
    val postViewModel: PostViewModel = viewModel()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by bottomBarNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                mainScreens.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            val icon = when (screen) {
                                Screen.Popular -> Icons.Filled.Star
                                Screen.Noticias -> Icons.Filled.Newspaper
                                Screen.Comunidad -> Icons.Filled.Comment
                                Screen.Profile -> Icons.Filled.AccountCircle
                                else -> Icons.Filled.Star
                            }
                            Icon(icon, contentDescription = null)
                        },
                        label = { 
                            val label = when (screen) {
                                Screen.Popular -> "Populares"
                                Screen.Noticias -> "Noticias"
                                Screen.Comunidad -> "Comunidad"
                                Screen.Profile -> "Perfil"
                                else -> ""
                            }
                            Text(label)
                        },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            bottomBarNavController.navigate(screen.route) {
                                popUpTo(bottomBarNavController.graph.findStartDestination().id) {
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
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(bottomBarNavController, startDestination = Screen.Popular.route) {
                composable(Screen.Popular.route) { PopularScreen(viewModel = postViewModel) }
                composable(Screen.Noticias.route) { NewsScreen(viewModel = postViewModel) }
                composable(Screen.Comunidad.route) { CommunityScreen(viewModel = postViewModel) }
                composable(Screen.Profile.route) { ProfileScreen(navController = mainNavController, authViewModel = authViewModel) } 
            }
        }
    }
}
fun HomeScreenPreview(){
    ProyectoAplicacionesTheme {
        HomeScreen(navController = rememberNavController())
    }
}
