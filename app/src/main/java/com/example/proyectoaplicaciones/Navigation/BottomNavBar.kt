package com.example.proyectoaplicaciones.Navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavBar(navController: NavController){
    val items = listOf(
        BottomNavItem(
            name = "Populares",
            route = Screen.Popular.route,
            icon = Icons.Default.Home
        ),
        BottomNavItem(
            name = "Noticias",
            route = Screen.Noticias.route,
            icon = Icons.Default.List
        ),
        BottomNavItem(
            name = "Comunidad",
            route = Screen.Comunidad.route,
            icon = Icons.Default.Face
        )
    )

    NavigationBar{
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = {Icon(item.icon, contentDescription = item.name)},
                label = {Text(item.name)},
                selected = currentRoute == item.route,
                onClick = {
                    if(currentRoute != item.route){
                        navController.navigate(item.route){
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}

data class BottomNavItem(
    val name: String,
    val route: String,
    val icon: ImageVector
)
