package com.example.proyectoaplicaciones.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoaplicaciones.Data.Model.ExternalGame
import com.example.proyectoaplicaciones.Data.Remote.ExternalRetrofitInstance
import com.example.proyectoaplicaciones.Repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.Exception

data class GameUiState(
    val games: List<ExternalGame> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class GameViewModel(private val gameRepository: GameRepository = GameRepository(ExternalRetrofitInstance.api)) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchPopularGames()
    }

    fun fetchPopularGames() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = gameRepository.getPopularGames()
                _uiState.update { it.copy(games = response.results) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error fetching games: " + e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}