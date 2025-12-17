package com.example.proyectoaplicaciones.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectoaplicaciones.data.model.Role
import com.example.proyectoaplicaciones.viewModel.AuthViewModel
import com.example.proyectoaplicaciones.viewModel.PostViewModel
import com.example.proyectoaplicaciones.viewModel.VoteType
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    postViewModel: PostViewModel
) {
    val selectedPost by postViewModel.selectedPost.collectAsState()
    val comments by postViewModel.comments.collectAsState()
    val favoritePosts by postViewModel.favoritePosts.collectAsState()
    val userVotes by postViewModel.userVotes.collectAsState()
    val authState by authViewModel.uiState.collectAsState()
    val error by postViewModel.error.collectAsState()

    var newCommentText by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val guestActionMessage = "Necesitas una cuenta para realizar esta acción."

    LaunchedEffect(error) {
        error?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
                postViewModel.clearError()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(selectedPost?.title ?: "Detalle del Post") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        selectedPost?.let { post ->
            LazyColumn(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
                item {
                    Text(post.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(post.body, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val currentVote = userVotes[post.id] ?: VoteType.NONE

                        val voteAction: (VoteType) -> Unit = { voteType ->
                            if (authState.isAuthenticated) {
                                postViewModel.handleVote(post.id, voteType)
                            } else {
                                scope.launch { snackbarHostState.showSnackbar(guestActionMessage) }
                            }
                        }

                        IconButton(onClick = { voteAction(VoteType.LIKE) }) {
                            Icon(Icons.Filled.ThumbUp, contentDescription = "Like", tint = if(currentVote == VoteType.LIKE) MaterialTheme.colorScheme.primary else LocalContentColor.current)
                        }
                        Text(post.score.toString())
                        IconButton(onClick = { voteAction(VoteType.DISLIKE) }) {
                            Icon(Icons.Filled.ThumbDown, contentDescription = "Dislike", tint = if(currentVote == VoteType.DISLIKE) MaterialTheme.colorScheme.primary else LocalContentColor.current)
                        }
                        Spacer(Modifier.weight(1f))

                        // --- INICIO DE LA CORRECCIÓN ---
                        val canDeletePost = authState.user?.let {
                            it.id == post.userId || it.role == Role.ADMIN || it.role == Role.MODERATOR
                        } ?: false

                        if (authState.isAuthenticated && canDeletePost) {
                            IconButton(onClick = {
                                postViewModel.deletePost(post.id)
                                navController.popBackStack()
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Borrar Post",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                        // --- FIN DE LA CORRECCIÓN ---

                        IconButton(onClick = {
                            if(authState.isAuthenticated) {
                                postViewModel.toggleFavorite(post)
                            } else {
                                scope.launch { snackbarHostState.showSnackbar(guestActionMessage) }
                            }
                        }) {
                            val isFavorite = favoritePosts.any { it.id == post.id }
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Favorito",
                                tint = if (isFavorite) MaterialTheme.colorScheme.primary else LocalContentColor.current
                            )
                        }
                    }
                    Divider(modifier = Modifier.padding(vertical = 16.dp))
                }

                item {
                    Text("Comentarios", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(comments) { comment ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(comment.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(comment.body, style = MaterialTheme.typography.bodyMedium)
                            }

                            // --- INICIO DE LA CORRECCIÓN ---
                            val canDeleteComment = authState.user?.let {
                                it.username == comment.name || it.role == Role.ADMIN || it.role == Role.MODERATOR
                            } ?: false

                            if (authState.isAuthenticated && canDeleteComment) {
                                IconButton(onClick = { postViewModel.deleteComment(comment.id) }) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Borrar Comentario",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                            // --- FIN DE LA CORRECCIÓN ---
                        }
                    }
                }

                if (authState.isAuthenticated) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = newCommentText,
                            onValueChange = { newCommentText = it },
                            label = { Text("Añadir un comentario") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(onClick = {
                                val authorName = authState.user?.username ?: "Anónimo"
                                postViewModel.addComment(newCommentText, authorName)
                                newCommentText = ""
                            }) {
                                Text("Publicar")
                            }
                        }
                    }
                }
            }
        }
    }
}
