package com.example.proyectoaplicaciones.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
fun NewsScreen(navController: NavController, viewModel: PostViewModel, authViewModel: AuthViewModel) {
    val newsPosts by viewModel.newsPosts.collectAsState()
    val authState by authViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {Text("Noticias de videojuegos")}
            )
        }
    ){ innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ){
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ){
                items(newsPosts){ post ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { 
                                viewModel.selectPost(post)
                                navController.navigate(Screen.PostDetail.route)
                            }
                    ){
                        Column(modifier = Modifier.padding(16.dp)){
                            Text(
                                text = post.title,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = post.body,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 3
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ThumbUp, contentDescription = "PuntuaciÃ³n", modifier = Modifier.size(16.dp))
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
