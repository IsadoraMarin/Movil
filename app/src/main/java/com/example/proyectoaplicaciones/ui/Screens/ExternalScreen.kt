package com.example.proyectoaplicaciones.ui.Screens

import androidx.compose.runtime.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectoaplicaciones.ViewModel.ExternalViewModel

@Composable
fun ExternalScreen(viewModel: ExternalViewModel) {

    val breeds = viewModel.breeds.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadBreeds()
    }

    LazyColumn {
        items(breeds.value) { breed ->
            Text(text = breed)
        }
    }
}