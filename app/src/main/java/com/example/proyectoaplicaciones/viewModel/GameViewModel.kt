package com.example.proyectoaplicaciones.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoaplicaciones.data.model.Game
import com.example.proyectoaplicaciones.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GameUiState(
    val games: List<Game> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class GameViewModel(private val repository: GameRepository = GameRepository()) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    fun fetchPopularGames() {
        viewModelScope.launch {
            _uiState.value = GameUiState(isLoading = true)
            try {
                val games = repository.getPopularGames()
                _uiState.value = GameUiState(games = games, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = GameUiState(error = "Error al cargar los juegos: ${e.message}", isLoading = false)
            }
        }
    }
}