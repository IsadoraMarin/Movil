package com.example.proyectoaplicaciones.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.proyectoaplicaciones.Model.PostData

class PostViewModel : ViewModel(){

    private val posts = mutableListOf(
        PostData(1, "Nuevo DLC de Elden Ring", "Detalles del contenido", "Noticias", 120, 40, "Admin", "2024/06/14"),
        PostData(2, "Los mejores juegos de simulador de granjas", "Juegos de administracion", "Popular", 300, 50, "UsuarioAburrido", "2024/07/16"),
        PostData(3, "Tutorial para hacer un juego basico en RPGMaker", "Como utilizar RPGmaker", "Comunidades", 130, 60, "YumeNikkeEnjoyer", "2024/09/17")
    )

    private val _posts = MutableLiveData<List<PostData>>()
    val postsLiveData: LiveData<List<PostData>> = _posts

    fun cargarPosts(categoria: String) {
        _posts.value = when (categoria) {
            "Popular" -> posts.sortedByDescending { it.likes + it.comentarios }.take(10)
            "News" -> posts.filter { it.categoria == "News" }
            "Community" -> posts.filter { it.categoria == "Community" }
            else -> emptyList()

        }
    }

    fun likePost(postId: Int) {
        posts.find { it.id == postId }?.let { it.likes++ }
        refresh()
    }

    fun addComentario(postId: Int) {
        posts.find { it.id == postId }?.let { it.comentarios++ }
        refresh()
    }

    private fun refresh() {
        _posts.value = _posts.value?.toList()
    }

}

