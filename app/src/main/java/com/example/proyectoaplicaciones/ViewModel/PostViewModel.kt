package com.example.proyectoaplicaciones.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoaplicaciones.Data.Model.Post
import com.example.proyectoaplicaciones.Repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PostViewModel : ViewModel(){
    private val repository = PostRepository()

    private val _popularPosts = MutableStateFlow<List<Post>>(emptyList())
    val popularPosts: StateFlow<List<Post>> = _popularPosts

    private val _newsPosts = MutableStateFlow<List<Post>>(emptyList())
    val newsPosts: StateFlow<List<Post>> = _newsPosts

    private val _communityPosts = MutableStateFlow<List<Post>>(emptyList())
    val communityPosts: StateFlow<List<Post>> = _communityPosts

    private val _postList = MutableStateFlow<List<Post>>(value = emptyList())

    val postList: StateFlow<List<Post>> = _postList

    init {
        fetchPosts()
    }

    private fun fetchPosts(){
        viewModelScope.launch {
            try {
                _popularPosts.value = repository.getPostsByCategory("Popular")
                _newsPosts.value = repository.getPostsByCategory("Noticias")
                _communityPosts.value = repository.getPostsByCategory("Comunidades")
                _postList.value = _popularPosts.value + _newsPosts.value + _communityPosts.value
            }catch (e: Exception){
                println("Error al obtener los datos: ${e.localizedMessage}")
            }
        }
    }

    fun publishPost(title: String, content: String, autor: String, category: String) {
        viewModelScope.launch {
            try {
                val newPost = Post(id = 0, title = title, body = content, userId = autor.toIntOrNull() ?: 0, category = category)
                repository.addPost(newPost)
                fetchPosts() // refrescar después de agregar
            } catch (e: Exception) {
                println("Error al publicar post: ${e.localizedMessage}")
            }
        }
    }

    fun addComment(postId: Int, comment: String, autor: String) {
        viewModelScope.launch {
            try {
                repository.addComment(postId, comment, autor)
                fetchPosts() // refrescar para mostrar comentarios actualizados
            } catch (e: Exception) {
                println("Error al agregar comentario: ${e.localizedMessage}")
            }
        }
    }

}
