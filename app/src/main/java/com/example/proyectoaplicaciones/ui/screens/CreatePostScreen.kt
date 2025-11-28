package com.example.proyectoaplicaciones.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectoaplicaciones.viewModel.AuthViewModel
import com.example.proyectoaplicaciones.viewModel.PostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(navController: NavController, authViewModel: AuthViewModel) { // Se recibe el ViewModel
    val postViewModel: PostViewModel = viewModel()
    // Se elimina la creación local de authViewModel
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var isTitleEmpty by remember { mutableStateOf(false) }
    var isContentEmpty by remember { mutableStateOf(false) }

    val authState by authViewModel.uiState.collectAsState()

    fun validateFields(): Boolean {
        isTitleEmpty = title.isBlank()
        isContentEmpty = content.isBlank()
        return !isTitleEmpty && !isContentEmpty
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Publicación") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it; isTitleEmpty = false },
                label = { Text("Título de la publicación") },
                isError = isTitleEmpty,
                modifier = Modifier.fillMaxWidth()
            )
            if (isTitleEmpty) {
                Text("El título no puede estar vacío", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = content,
                onValueChange = { content = it; isContentEmpty = false },
                label = { Text("¿Qué estás pensando?") },
                isError = isContentEmpty,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            if (isContentEmpty) {
                Text("El contenido no puede estar vacío", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    if (validateFields()) {
                        val userId = authState.user?.id ?: -1
                        if (userId != -1) {
                            postViewModel.publishPost(
                                title = title,
                                content = content,
                                autorId = userId
                            )
                            navController.popBackStack()
                        }
                    }
                },
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Publicar", fontSize = 16.sp)
            }
        }
    }
}
