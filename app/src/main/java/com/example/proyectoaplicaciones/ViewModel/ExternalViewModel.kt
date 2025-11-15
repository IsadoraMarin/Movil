package com.example.proyectoaplicaciones.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoaplicaciones.Repository.ExternalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExternalViewModel(
    private val repo: ExternalRepository
) : ViewModel(){

    private val _breeds = MutableStateFlow<List<String>>(emptyList())
    val breeds: StateFlow<List<String>> = _breeds

    fun loadBreeds(){
        viewModelScope.launch {
            _breeds.value = repo.fetchBreeds()
        }
    }
}