package com.example.proyectoaplicaciones.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Register : Screen("register")
    object Popular : Screen("popular")
    object Community : Screen("community")
    object News : Screen("news")
    object Games : Screen("games")
    object Profile : Screen("profile")
    object Favorites : Screen("favorites")
    object PostDetail : Screen("post_detail")
    object CreatePost : Screen("create_post")
    object EditProfile : Screen("edit_profile")
}
