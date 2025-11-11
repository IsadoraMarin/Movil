package com.example.proyectoaplicaciones.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoaplicaciones.Repository.PostRepository
import com.example.proyectoaplicaciones.Data.Model.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PostViewModel : ViewModel(){
    private val repository = PostRepository()

    private val _postList = MutableStateFlow<List<Post>>(value = emptyList())

    val postList: StateFlow<List<Post>> = _postList

    init {
        fetchPosts()
    }

    private fun fetchPosts(){
        viewModelScope.launch {
            try {
                _postList.value = repository.getPosts()
            }catch (e: Exception){
                println("Error al obtener los datos: ${e.localizedMessage}")
            }
        }
    }
}

