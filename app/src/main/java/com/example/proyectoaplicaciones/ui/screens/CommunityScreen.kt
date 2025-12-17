package com.example.proyectoaplicaciones.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.* 
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectoaplicaciones.data.model.Role
import com.example.proyectoaplicaciones.navigation.Screen
import com.example.proyectoaplicaciones.viewModel.AuthViewModel
import com.example.proyectoaplicaciones.viewModel.PostViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    navController: NavController, 
    authViewModel: AuthViewModel, 
    postViewModel: PostViewModel
) {
    val communityPosts by postViewModel.communityPosts.collectAsState()
    val authState by authViewModel.uiState.collectAsState()
    val error by postViewModel.error.collectAsState()
    val isLoading by postViewModel.isLoading.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(error) {
        error?.let {
            scope.launch {
                snackbarHostState.showSnackbar(message = it, duration = SnackbarDuration.Short)
                postViewModel.clearError()
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        postViewModel.fetchPosts()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            if (authState.isAuthenticated) { 
                FloatingActionButton(onClick = { navController.navigate(Screen.CreatePost.route) }) {
                    Icon(Icons.Filled.Add, contentDescription = "Crear publicación")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading && communityPosts.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
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
                                    Icon(Icons.Filled.ThumbUp, contentDescription = "Puntuación", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(post.score.toString(), style = MaterialTheme.typography.bodySmall)
                                    Spacer(modifier = Modifier.weight(1f))
                                    
                                    // --- INICIO DE LA CORRECCIÓN ---
                                    val canDelete = authState.user?.let {
                                        it.id == post.userId || it.role == Role.ADMIN || it.role == Role.MODERATOR
                                    } ?: false

                                    if (authState.isAuthenticated && canDelete) {
                                        IconButton(onClick = { postViewModel.deletePost(post.id) }) {
                                            Icon(Icons.Filled.Delete, contentDescription = "Eliminar Post", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                    // --- FIN DE LA CORRECCIÓN ---
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
