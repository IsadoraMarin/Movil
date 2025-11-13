package com.example.proyectoaplicaciones.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectoaplicaciones.viewmodel.AuthViewModel
import com.example.proyectoaplicaciones.viewmodel.PostViewModel
import com.example.proyectoaplicaciones.viewmodel.VoteType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(navController: NavController, postViewModel: PostViewModel, authViewModel: AuthViewModel) {
    val selectedPost by postViewModel.selectedPost.collectAsState()
    val comments by postViewModel.comments.collectAsState()
    val authState by authViewModel.uiState.collectAsState()
    val favoritePosts by postViewModel.favoritePosts.collectAsState()
    val userVotes by postViewModel.userVotes.collectAsState()

    var newCommentText by remember { mutableStateOf("") }

    val post = selectedPost ?: return
    val isFavorite = favoritePosts.any { it.id == post.id }
    val userVote = userVotes[post.id] ?: VoteType.NONE

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(post.title, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if(authState.isAuthenticated){
                        IconButton(onClick = { postViewModel.toggleFavorite(post) }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                tint = if (isFavorite) MaterialTheme.colorScheme.primary else LocalContentColor.current,
                                contentDescription = "Marcar como Favorito"
                            )
                        }
                    }
                }
            )
        }
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(it)) {
            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp).verticalScroll(rememberScrollState())
            ) {
                // ... Contenido del post y valoraciones ...
                
                Spacer(modifier = Modifier.height(24.dp))
                Text("Comentarios", style = MaterialTheme.typography.headlineSmall)

                // Lista de comentarios
                comments.forEach { comment ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(comment.name, fontWeight = FontWeight.Bold)
                                Text(comment.body)
                            }
                            // Botón de eliminar visible para el autor o un moderador
                            if (authState.isModerator || authState.username == comment.name) {
                                IconButton(onClick = { postViewModel.deleteComment(comment.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar Comentario", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
            
            // Campo para añadir un nuevo comentario
            if (authState.isAuthenticated) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newCommentText,
                        onValueChange = { newCommentText = it },
                        label = { Text("Escribe un comentario...") },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        if (newCommentText.isNotBlank()) {
                            postViewModel.addComment(newCommentText, authState.username)
                            newCommentText = ""
                        }
                    }) {
                        Icon(Icons.Default.Send, contentDescription = "Enviar comentario")
                    }
                }
            }
        }
    }
}
