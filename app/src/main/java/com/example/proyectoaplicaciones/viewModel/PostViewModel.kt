package com.example.proyectoaplicaciones.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoaplicaciones.data.model.Comentarios
import com.example.proyectoaplicaciones.data.model.Post
import com.example.proyectoaplicaciones.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class VoteType { LIKE, DISLIKE, NONE }

class PostViewModel(private val postRepository: PostRepository = PostRepository()) : ViewModel() {

    private val _popularPosts = MutableStateFlow<List<Post>>(emptyList())
    val popularPosts: StateFlow<List<Post>> = _popularPosts

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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun fetchPosts() {
        viewModelScope.launch {
            _error.value = null
            _isLoading.value = true
            try {
                val allPosts = postRepository.getPosts()
                _popularPosts.value = allPosts.sortedByDescending { it.score }.take(10)
                _communityPosts.value = allPosts.sortedByDescending { it.id }
            } catch (e: Exception) {
                _error.value = "Error al cargar las publicaciones: ${e.message}"
                _popularPosts.value = emptyList()
                _communityPosts.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectPost(post: Post) {
        _selectedPost.value = post
        viewModelScope.launch {
            _error.value = null
            try {
                _comments.value = postRepository.getComments(post.id)
            } catch (e: Exception) {
                _error.value = "Error al cargar los comentarios: ${e.message}"
                _comments.value = emptyList()
            }
        }
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
        val oldPopularPosts = _popularPosts.value
        val oldCommunityPosts = _communityPosts.value
        val oldSelectedPost = _selectedPost.value
        val oldUserVotes = _userVotes.value

        val currentVoteInMap = oldUserVotes[postId] ?: VoteType.NONE
        val updateAction = { post: Post -> post.updateScore(currentVoteInMap, vote) }

        _popularPosts.update { list -> list.map { if (it.id == postId) updateAction(it) else it } }
        _communityPosts.update { list -> list.map { if (it.id == postId) updateAction(it) else it } }
        if (_selectedPost.value?.id == postId) {
            _selectedPost.update { it?.let(updateAction) }
        }
        _userVotes.update { currentVotes ->
            currentVotes.toMutableMap().apply {
                this[postId] = if (currentVoteInMap == vote) VoteType.NONE else vote
            }
        }

        viewModelScope.launch {
            _error.value = null
            try {
                postRepository.ratePost(postId, vote == VoteType.LIKE)
            } catch (e: Exception) {
                _error.value = "Error al registrar el voto. Inténtalo de nuevo."
                _popularPosts.value = oldPopularPosts
                _communityPosts.value = oldCommunityPosts
                _selectedPost.value = oldSelectedPost
                _userVotes.value = oldUserVotes
            }
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
            _error.value = null
            try {
                val newPost = Post(userId = autorId, id = 0, title = title, body = content, score = 0)
                postRepository.addPost(newPost)
                fetchPosts()
            } catch (e: Exception) {
                _error.value = "Error al publicar el post: ${e.message}"
            }
        }
    }

    fun deletePost(postId: Int) {
        viewModelScope.launch {
            _error.value = null
            try {
                postRepository.deletePost(postId)
                fetchPosts()
            } catch (e: Exception) {
                _error.value = "Error al eliminar el post: ${e.message}"
            }
        }
    }

    fun addComment(commentBody: String, author: String) {
        selectedPost.value?.id?.let { postId ->
            viewModelScope.launch {
                _error.value = null
                try {
                    postRepository.addComment(postId, commentBody, author)
                    _comments.value = postRepository.getComments(postId)
                } catch (e: Exception) {
                    _error.value = "Error al añadir el comentario: ${e.message}"
                }
            }
        }
    }

    fun deleteComment(commentId: Int) {
        viewModelScope.launch {
            _error.value = null
            try {
                // Lógica futura para eliminar comentario
            } catch (e: Exception) {
                _error.value = "Error al eliminar el comentario: ${e.message}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
