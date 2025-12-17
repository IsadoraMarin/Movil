package com.example.proyectoaplicaciones.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectoaplicaciones.navigation.Screen
import com.example.proyectoaplicaciones.viewModel.PostViewModel

@Composable
fun PopularScreen(navController: NavController, postViewModel: PostViewModel) {
    val popularPosts by postViewModel.popularPosts.collectAsState()
    val isLoading by postViewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        postViewModel.fetchPosts()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading && popularPosts.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(popularPosts) { post ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                postViewModel.selectPost(post)
                                navController.navigate(Screen.PostDetail.route)
                            }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(post.title, style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(post.body, style = MaterialTheme.typography.bodyMedium, maxLines = 3)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // --- CORREGIDO ---
                                Icon(Icons.Filled.ThumbUp, contentDescription = "Puntuación", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(post.score.toString(), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}