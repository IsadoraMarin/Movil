package com.example.proyectoaplicaciones.Navigation

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.proyectoaplicaciones.ViewModel.PostViewModel
import com.example.proyectoaplicaciones.ui.Screens.PopularScreen
import com.example.proyectoaplicaciones.ui.Screens.CommunityScreen
import com.example.proyectoaplicaciones.ui.Screens.NewsScreen
import com.example.proyectoaplicaciones.ui.Screens.HomeScreen


sealed class Screen(val route: String){

    object Home : Screen("Home")
    object Popular : Screen("Popular")
    object Noticias : Screen("Noticias")
    object Comunidad : Screen("Comunidad")
}

@Composable
fun AppNavigation(navController: NavHostController, viewModel: PostViewModel) {
   Scaffold(
       bottomBar = {BottomNavBar(navController)}
   ){ innerPadding ->
       NavHost(
           navController = navController,
           startDestination = Screen.Popular.route,
           modifier = Modifier.padding(innerPadding)
       ){
           composable(Screen.Home.route){
               HomeScreen(navController)
           }
           composable(Screen.Popular.route){
               PopularScreen(viewModel)
           }
           composable(Screen.Noticias.route){
               NewsScreen(viewModel)
           }
           composable(Screen.Comunidad.route){
               CommunityScreen(viewModel)
           }
       }
   }
}
