package com.example.proyectoaplicaciones.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoaplicaciones.Data.Model.Comentarios
import com.example.proyectoaplicaciones.Data.Model.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class VoteType { LIKE, DISLIKE, NONE }

class PostViewModel : ViewModel() {

    private val _popularPosts = MutableStateFlow<List<Post>>(emptyList())
    val popularPosts: StateFlow<List<Post>> = _popularPosts

    private val _newsPosts = MutableStateFlow<List<Post>>(emptyList())
    val newsPosts: StateFlow<List<Post>> = _newsPosts

    private val _communityPosts = MutableStateFlow<List<Post>>(emptyList())
    val communityPosts: StateFlow<List<Post>> = _communityPosts

    private val _selectedPost = MutableStateFlow<Post?>(null)
    val selectedPost: StateFlow<Post?> = _selectedPost

    private val _comments = MutableStateFlow<List<Comentarios>>(emptyList())
    val comments: StateFlow<List<Comentarios>> = _comments

    private val _favoritePosts = MutableStateFlow<List<Post>>(emptyList())
    val favoritePosts: StateFlow<List<Post>> = _favoritePosts

    private val _userVotes = MutableStateFlow<Map<Int, VoteType>>(emptyMap())
    val userVotes: StateFlow<Map<Int, VoteType>> = _userVotes

    init {
        fetchPosts()
    }

    fun selectPost(post: Post) {
        _selectedPost.value = post
        loadCommentsForSelectedPost()
    }

    fun publishPost(title: String, content: String, autorId: Int) {
        viewModelScope.launch {
            val newPost = Post(
                userId = autorId, 
                id = (_popularPosts.value.size + _newsPosts.value.size + _communityPosts.value.size + 1), 
                title = title, 
                body = content
            )
            _communityPosts.update { it + newPost }
        }
    }

    fun deletePost(postId: Int) {
        _popularPosts.update { list -> list.filterNot { it.id == postId } }
        _newsPosts.update { list -> list.filterNot { it.id == postId } }
        _communityPosts.update { list -> list.filterNot { it.id == postId } }
        _favoritePosts.update { list -> list.filterNot { it.id == postId } }
    }

    private fun loadCommentsForSelectedPost() {
        _comments.value = when (_selectedPost.value?.id) {
            101 -> listOf(Comentarios(1, "Usuario_Gamer", "", "¡Totalmente de acuerdo! La expansión es una obra maestra."))
            201 -> listOf(
                Comentarios(2, "LaraCroft_Fan", "", "Gracias por la guía, ¡justo lo que necesitaba!"),
                Comentarios(3, "RPG_Master", "", "Ojo, que hay un final secreto si completas la misión del Gnomo de Jardín.")
            )
            301 -> listOf(Comentarios(4, "xX_Slayer_Xx", "", "¡Yo me apunto! Te agrego esta noche, mi ID es Slayer_92."))
            else -> emptyList()
        }
    }

    fun addComment(commentBody: String, author: String) {
        val newComment = Comentarios(id = (_comments.value.size + 100), name = author, email = "", body = commentBody)
        _comments.update { it + newComment }
    }

    fun deleteComment(commentId: Int) {
        _comments.update { list -> list.filterNot { it.id == commentId } }
    }

    fun handleVote(postId: Int, vote: VoteType) {
        val currentVote = _userVotes.value[postId] ?: VoteType.NONE

        _popularPosts.update { list -> list.map { if (it.id == postId) it.updateCounters(currentVote, vote) else it } }
        _newsPosts.update { list -> list.map { if (it.id == postId) it.updateCounters(currentVote, vote) else it } }
        _communityPosts.update { list -> list.map { if (it.id == postId) it.updateCounters(currentVote, vote) else it } }
        if (_selectedPost.value?.id == postId) {
            _selectedPost.update { it?.updateCounters(currentVote, vote) }
        }

        _userVotes.update { currentVotes ->
            currentVotes.toMutableMap().apply {
                this[postId] = if (currentVote == vote) VoteType.NONE else vote
            }
        }
    }
    
    private fun Post.updateCounters(currentVote: VoteType, newVote: VoteType): Post {
        var newLikes = this.likes
        var newDislikes = this.dislikes

        if (currentVote == newVote) { 
            if (newVote == VoteType.LIKE) newLikes-- else newDislikes--
        } else { 
            if (newVote == VoteType.LIKE) newLikes++ else newDislikes++
            if (currentVote == VoteType.DISLIKE) newDislikes--
            if (currentVote == VoteType.LIKE) newLikes--
        }
        return this.copy(likes = newLikes, dislikes = newDislikes)
    }

    fun toggleFavorite(post: Post) {
        _favoritePosts.update { currentFavorites ->
            if (currentFavorites.any { it.id == post.id }) {
                currentFavorites.filterNot { it.id == post.id }
            } else {
                currentFavorites + post
            }
        }
    }

    fun isFavorite(post: Post): Boolean {
        return _favoritePosts.value.any { it.id == post.id }
    }

    private fun fetchPosts() {
        viewModelScope.launch {
            _popularPosts.value = createPopularPosts()
            _newsPosts.value = createNewsPosts()
            _communityPosts.value = createCommunityPosts()
        }
    }

    private fun createNewsPosts(): List<Post> {
        return listOf(
            Post(1, 101, "Análisis de \"Elden Ring: Shadow of the Erdtree\"", "...", 120, 5),
            Post(2, 102, "El estudio indie que está revolucionando la estrategia", "...", 85, 2),
            Post(3, 103, "Confirmado: La nueva consola de Nintendo llegará en 2025", "...", 250, 12)
        )
    }

    private fun createPopularPosts(): List<Post> {
        return listOf(
            Post(4, 201, "Guía Definitiva: Todos los finales de \"Baldur's Gate 3\"", "...", 500, 10),
            Post(5, 202, "Debate: ¿Es \"Helldivers 2\" el mejor juego como servicio?", "...", 350, 25),
            Post(6, 203, "Los 10 builds más rotos para empezar en \"Diablo IV\"", "...", 420, 8)
        )
    }

    private fun createCommunityPosts(): List<Post> {
        return listOf(
            Post(7, 301, "Busco equipo para subir a Inmortal en Valorant", "...", 15, 0),
            Post(8, 302, "¿Alguien me ayuda con el jefe final de Sekiro?", "...", 30, 1),
            Post(9, 303, "¿Qué opinan de la última actualización de Fortnite?", "...", 50, 3)
        )
    }
}
