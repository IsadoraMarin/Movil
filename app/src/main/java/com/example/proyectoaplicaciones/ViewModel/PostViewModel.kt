package com.example.proyectoaplicaciones.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoaplicaciones.Data.Model.Comentarios
import com.example.proyectoaplicaciones.Data.Model.Post
import com.example.proyectoaplicaciones.Data.Remote.RetrofitInstance
import com.example.proyectoaplicaciones.Repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class VoteType { LIKE, DISLIKE, NONE }

class PostViewModel(private val postRepository: PostRepository = PostRepository(RetrofitInstance.api)) : ViewModel() {

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

    private fun fetchPosts() {
        viewModelScope.launch {
            try {
                val allPosts = postRepository.getPosts()
                _newsPosts.value = allPosts.filter { it.id in 1..99 }
                _popularPosts.value = allPosts.sortedByDescending { it.score }.take(10)
                _communityPosts.value = allPosts.filter { it.id >= 100 }
            } catch (e: Exception) {
                // Manejo de errores
            }
        }
    }

    fun selectPost(post: Post) {
        _selectedPost.value = post
        // Aquí se debería cargar comentarios desde el repositorio
        // _comments.value = postRepository.getComments(post.id)
    }

    private fun Post.updateScore(currentVote: VoteType, newVote: VoteType): Post {
        var newScore = this.score
        if (currentVote == newVote) { 
            if (newVote == VoteType.LIKE) newScore-- else newScore++
        } else { 
            if (newVote == VoteType.LIKE) newScore++ else newScore--
            if (currentVote == VoteType.DISLIKE) newScore++
            if (currentVote == VoteType.LIKE) newScore--
        }
        return this.copy(score = newScore)
    }

    fun handleVote(postId: Int, vote: VoteType) {
        val currentVote = _userVotes.value[postId] ?: VoteType.NONE
        val updateAction = { post: Post -> post.updateScore(currentVote, vote) }

        _popularPosts.update { list -> list.map { if (it.id == postId) updateAction(it) else it } }
        _newsPosts.update { list -> list.map { if (it.id == postId) updateAction(it) else it } }
        _communityPosts.update { list -> list.map { if (it.id == postId) updateAction(it) else it } }
        if (_selectedPost.value?.id == postId) {
            _selectedPost.update { it?.let(updateAction) }
        }

        _userVotes.update { currentVotes ->
            currentVotes.toMutableMap().apply {
                this[postId] = if (currentVote == vote) VoteType.NONE else vote
            }
        }
        viewModelScope.launch {
           // postRepository.ratePost(postId, vote == VoteType.LIKE) // Notificar al backend
        }
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

    fun publishPost(title: String, content: String, autorId: Int) {
        viewModelScope.launch {
            val newPost = Post(
                userId = autorId, 
                id = 0, // El ID debería ser generado por el backend
                title = title, 
                body = content,
                score = 0
            )
            postRepository.addPost(newPost)
            fetchPosts() // Recargar posts
        }
    }

    fun deletePost(postId: Int) {
        viewModelScope.launch {
            postRepository.deletePost(postId)
            fetchPosts() // Recargar posts
        }
    }

    fun addComment(commentBody: String, author: String) {
        selectedPost.value?.id?.let {
            viewModelScope.launch {
                postRepository.addComment(it, commentBody, author)
                // Recargar comentarios
            }
        }
    }

    fun deleteComment(commentId: Int) {
        viewModelScope.launch {
            // postRepository.deleteComment(commentId)
            // Recargar comentarios
        }
    }
}