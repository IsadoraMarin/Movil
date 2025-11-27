package com.example.proyectoaplicaciones.ui.Screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectoaplicaciones.Data.Model.Role
import com.example.proyectoaplicaciones.Navigation.Screen
import com.example.proyectoaplicaciones.viewmodel.AuthViewModel
import com.example.proyectoaplicaciones.viewmodel.PostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(navController: NavController, viewModel: PostViewModel, authViewModel: AuthViewModel) {
    val communityPosts by viewModel.communityPosts.collectAsState()
    val authState by authViewModel.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            if (authState.isAuthenticated) { // Solo mostrar si está autenticado
                FloatingActionButton(onClick = { navController.navigate(Screen.CreatePost.route) }) {
                    Icon(Icons.Default.Add, contentDescription = "Crear publicación")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(communityPosts) { post ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { 
                                viewModel.selectPost(post)
                                navController.navigate(Screen.PostDetail.route)
                            }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(post.title, style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(post.body, style = MaterialTheme.typography.bodyMedium, maxLines = 3)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ThumbUp, contentDescription = "Puntuación", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(post.score.toString(), style = MaterialTheme.typography.bodySmall)
                                Spacer(modifier = Modifier.weight(1f))
                                if (authState.user?.role == Role.MODERATOR) {
                                    IconButton(onClick = { viewModel.deletePost(post.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar Post", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                                Text("Autor ID: ${post.userId}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
