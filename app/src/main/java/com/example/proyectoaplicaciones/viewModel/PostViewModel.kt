package com.example.proyectoaplicaciones.viewModel

import android.util.Log
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

class PostViewModel(
    private val postRepository: PostRepository = PostRepository()
) : ViewModel() {

    private val _popularPosts = MutableStateFlow<List<Post>>(emptyList())
    val popularPosts: StateFlow<List<Post>> = _popularPosts.asStateFlow()

    private val _communityPosts = MutableStateFlow<List<Post>>(emptyList())
    val communityPosts: StateFlow<List<Post>> = _communityPosts.asStateFlow()

    private val _selectedPost = MutableStateFlow<Post?>(null)
    val selectedPost: StateFlow<Post?> = _selectedPost.asStateFlow()

    private val _comments = MutableStateFlow<List<Comentarios>>(emptyList())
    val comments: StateFlow<List<Comentarios>> = _comments.asStateFlow()

    private val _favoritePosts = MutableStateFlow<List<Post>>(emptyList())
    val favoritePosts: StateFlow<List<Post>> = _favoritePosts.asStateFlow()

    private val _userVotes = MutableStateFlow<Map<Int, VoteType>>(emptyMap())
    val userVotes: StateFlow<Map<Int, VoteType>> = _userVotes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun fetchPosts() {
        viewModelScope.launch {
            _error.value = null
            _isLoading.value = true

            try {
                val allPosts = postRepository.getPosts()
                _popularPosts.value = allPosts
                    .sortedByDescending { it.score }
                    .take(10)
                _communityPosts.value = allPosts
                    .sortedByDescending { it.id }
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error fetching posts", e)
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
        loadComments(post.id)
    }

    private fun loadComments(postId: Int) {
        viewModelScope.launch {
            _error.value = null

            try {
                val loadedComments = postRepository.getComments(postId)
                _comments.value = loadedComments
                Log.d("PostViewModel", "Loaded ${loadedComments.size} comments")
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error loading comments", e)
                _error.value = "Error al cargar los comentarios: ${e.message}"
                _comments.value = emptyList()
            }
        }
    }

    private fun Post.updateScore(currentVote: VoteType, newVote: VoteType): Post {
        var newScore = this.score

        if (currentVote == newVote) {
            newScore += if (newVote == VoteType.LIKE) -1 else 1
        } else {
            when {
                newVote == VoteType.LIKE -> {
                    newScore++
                    if (currentVote == VoteType.DISLIKE) newScore++
                }
                newVote == VoteType.DISLIKE -> {
                    newScore--
                    if (currentVote == VoteType.LIKE) newScore--
                }
            }
        }

        return this.copy(score = newScore)
    }

    fun handleVote(postId: Int, vote: VoteType) {
        val oldPopularPosts = _popularPosts.value
        val oldCommunityPosts = _communityPosts.value
        val oldSelectedPost = _selectedPost.value
        val oldUserVotes = _userVotes.value

        val currentVote = oldUserVotes[postId] ?: VoteType.NONE
        val newVote = if (currentVote == vote) VoteType.NONE else vote

        val updateAction = { post: Post -> post.updateScore(currentVote, vote) }

        _popularPosts.update { list ->
            list.map { if (it.id == postId) updateAction(it) else it }
        }
        _communityPosts.update { list ->
            list.map { if (it.id == postId) updateAction(it) else it }
        }

        if (_selectedPost.value?.id == postId) {
            _selectedPost.update { it?.let(updateAction) }
        }

        _userVotes.update { currentVotes ->
            currentVotes.toMutableMap().apply { this[postId] = newVote }
        }

        viewModelScope.launch {
            _error.value = null

            try {
                postRepository.ratePost(postId, vote == VoteType.LIKE)
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error voting on post", e)
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
            _isLoading.value = true

            try {
                val newPost = Post(
                    userId = autorId,
                    id = 0,
                    title = title,
                    body = content,
                    score = 0
                )
                postRepository.addPost(newPost)
                fetchPosts()
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error publishing post", e)
                _error.value = "Error al publicar el post: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deletePost(postId: Int) {
        viewModelScope.launch {
            _error.value = null
            _isLoading.value = true

            try {
                postRepository.deletePost(postId)
                fetchPosts()
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error deleting post", e)
                _error.value = "Error al eliminar el post: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addComment(commentBody: String, author: String) {
        if (commentBody.isBlank()) {
            _error.value = "El comentario no puede estar vacío"
            return
        }

        selectedPost.value?.id?.let { postId ->
            viewModelScope.launch {
                _error.value = null

                try {
                    postRepository.addComment(postId, commentBody, author)
                    loadComments(postId)
                } catch (e: Exception) {
                    Log.e("PostViewModel", "Error adding comment", e)
                    _error.value = "Error al añadir el comentario: ${e.message}"
                }
            }
        }
    }

    // --- INICIO DE LA CORRECCIÓN ---
    fun deleteComment(commentId: Int) {
        val originalComments = _comments.value // 1. Guardar el estado actual para rollback

        // 2. Actualizar la UI inmediatamente (actualización optimista)
        _comments.update { currentComments ->
            currentComments.filterNot { it.id == commentId }
        }

        viewModelScope.launch {
            _error.value = null
            try {
                // 3. Intentar borrar en el servidor
                postRepository.deleteComment(commentId)
                // Si tiene éxito, no hacemos nada. La UI ya está actualizada.
                Log.d("PostViewModel", "Comment $commentId deleted successfully from server.")
            } catch (e: Exception) {
                // 4. Si falla, revertir el cambio en la UI y mostrar un error
                Log.e("PostViewModel", "Error deleting comment from server", e)
                _error.value = "Error al eliminar el comentario. Inténtalo de nuevo."
                _comments.value = originalComments // Rollback
            }
        }
    }
    // --- FIN DE LA CORRECCIÓN ---

    fun clearError() {
        _error.value = null
    }
}