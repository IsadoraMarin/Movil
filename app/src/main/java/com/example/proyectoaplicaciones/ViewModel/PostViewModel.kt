package com.example.proyectoaplicaciones.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoaplicaciones.Data.Model.Comentarios
import com.example.proyectoaplicaciones.Data.Model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    }

    fun isFavorite(post: Post): Boolean {
        return _favoritePosts.value.any { it.id == post.id }
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

    private fun fetchPosts() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { // <- ¡ESTA ES LA CORRECCIÓN CLAVE!
                val news = createNewsPosts()
                val popular = createPopularPosts()
                val community = createCommunityPosts()
                _newsPosts.value = news
                _popularPosts.value = popular
                _communityPosts.value = community
            }
        }
    }

    private fun createNewsPosts(): List<Post> {
        return listOf(
            Post(1, 101, "Análisis de \"Elden Ring: Shadow of the Erdtree\"", "Tras decenas de horas, te contamos qué nos ha parecido la esperada expansión de FromSoftware. ¿Merece la pena volver a las Tierras Intermedias?", 115),
            Post(2, 102, "El estudio indie que está revolucionando la estrategia", "Conoce a Pathea Games, el equipo detrás de \"My Time at Sandrock\" y su innovadora forma de enfocar los juegos de gestión.", 83),
            Post(3, 103, "Confirmado: La nueva consola de Nintendo llegará en 2025", "La compañía ha anunciado oficialmente que su próxima plataforma de hardware se lanzará durante el primer trimestre del próximo año.", 238)
        )
    }

    private fun createPopularPosts(): List<Post> {
        return listOf(
            Post(4, 201, "Guía Definitiva: Todos los finales de \"Baldur's Gate 3\"", "¿Quieres saber cómo conseguir el mejor (o el peor) final para tus personajes? Aquí te explicamos todas las rutas y decisiones clave.", 490),
            Post(5, 202, "Debate: ¿Es \"Helldivers 2\" el mejor juego como servicio?", "Millones de jugadores luchan por la Supertierra, pero ¿qué es lo que hace a su modelo de monetización y contenido tan popular y justo?", 325),
            Post(6, 203, "Los 10 builds más rotos para empezar en \"Diablo IV\"", "Prepárate para la nueva expansión con estas configuraciones de personaje que te permitirán arrasar con todo desde el primer minuto.", 412)
        )
    }

    private fun createCommunityPosts(): List<Post> {
        return listOf(
            Post(7, 301, "Busco equipo para subir a Inmortal en Valorant", "Soy main Omen/Cypher y estoy atascado en Ascendente 3. Busco gente seria para jugar por las noches y que sepa comunicarse. ¡No más tóxicos!", 15),
            Post(8, 302, "¿Alguien me ayuda con el jefe final de Sekiro?", "Llevo tres días intentando derrotar a Isshin, el Maestro de la Espada, y es imposible. Cualquier consejo o si alguien puede unirse en cooperativo, lo agradecería.", 29),
            Post(9, 303, "¿Qué opinan de la última actualización de Fortnite?", "La nueva temporada ha cambiado completamente el meta. ¿Os gusta el nuevo mapa? ¿Qué arma creéis que está más OP ahora mismo?", 47)
        )
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
}
