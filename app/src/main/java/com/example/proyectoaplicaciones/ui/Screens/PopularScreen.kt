package com.example.proyectoaplicaciones.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectoaplicaciones.ViewModel.PostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PopularScreen(viewModel: PostViewModel){
    val posts = viewModel.postList.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = {Text("Posts mas populares")}
            )
        }
    ){ innerpadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerpadding)
        ){
            LazyColumn(
              modifier = Modifier
                  .fillMaxSize()
                  .padding(16.dp)
            ) {
                items(posts){ posts ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ){
                        Column(modifier = Modifier.padding(16.dp)){
                            Text(
                                text = "Titulo: ${posts.title}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = posts.content,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
