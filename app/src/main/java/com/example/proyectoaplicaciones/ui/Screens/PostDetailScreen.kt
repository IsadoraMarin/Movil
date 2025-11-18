package com.example.proyectoaplicaciones.ui.screens

import androidx.compose.foundation.layout.*
import com.example.proyectoaplicaciones.Data.Model.Role
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
                Text(text = post.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Por Usuario #${post.userId}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Divider(modifier = Modifier.padding(vertical = 16.dp))
                Text(text = post.body, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))

                // Fila de valoraciones simplificada
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { postViewModel.handleVote(post.id, VoteType.LIKE) }) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = "Me Gusta",
                            tint = if (userVote == VoteType.LIKE) MaterialTheme.colorScheme.primary else LocalContentColor.current
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(post.score.toString(), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { postViewModel.handleVote(post.id, VoteType.DISLIKE) }) {
                        Icon(
                            imageVector = Icons.Default.ThumbDown,
                            contentDescription = "No me Gusta",
                            tint = if (userVote == VoteType.DISLIKE) MaterialTheme.colorScheme.error else LocalContentColor.current
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("Comentarios", style = MaterialTheme.typography.headlineSmall)
                
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
                            if (authState.user?.role == Role.MODERATOR || authState.user?.username == comment.name) {
                                IconButton(onClick = { postViewModel.deleteComment(comment.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar Comentario", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
            
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
                            authState.user?.username?.let { authorName ->
                                postViewModel.addComment(newCommentText, authorName)
                                newCommentText = ""
                            }
                        }
                    }) {
                        Icon(Icons.Default.Send, contentDescription = "Enviar comentario")
                    }
                }
            }
        }
    }
}