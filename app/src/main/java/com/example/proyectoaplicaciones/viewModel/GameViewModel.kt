package com.example.proyectoaplicaciones.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoaplicaciones.data.model.Game
import com.example.proyectoaplicaciones.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
            _uiState.update { it.copy(isLoading = true, error = null) } // Usar copy
            try {
                val games = repository.getPopularGames()
                _uiState.update { it.copy(games = games, isLoading = false) } // Usar copy
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al cargar los juegos: ${e.message}", isLoading = false) } // Usar copy
            }
        }
    }
}