package com.example.proyectoaplicaciones.viewmodel

import com.example.proyectoaplicaciones.Data.Model.ExternalGame
import com.example.proyectoaplicaciones.Data.Model.GameListResponse
import com.example.proyectoaplicaciones.Repository.GameRepository
import com.example.proyectoaplicaciones.rules.InstantExecutorExtension
import com.example.proyectoaplicaciones.rules.MainDispatcherRule
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension

@ExperimentalCoroutinesApi
@ExtendWith(InstantExecutorExtension::class)
class GameViewModelTest {

    @JvmField
    @RegisterExtension
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var gameRepository: GameRepository
    private lateinit var gameViewModel: GameViewModel

    @BeforeEach
    fun setUp() {
        gameRepository = mockk()
    }

    @Test
    fun `fetchPopularGames success should update uiState correctly`() = runTest {
        // Arrange
        val games = listOf(
            ExternalGame(id = 1, name = "Game 1", released = "2023-01-01", backgroundImage = "background1", rating = 4.5),
            ExternalGame(id = 2, name = "Game 2", released = "2023-01-02", backgroundImage = "background2", rating = 4.8)
        )
        val gameListResponse = GameListResponse(results = games)
        coEvery { gameRepository.getPopularGames() } returns gameListResponse
        gameViewModel = GameViewModel(gameRepository)

        // Assert
        val state = gameViewModel.uiState.value
        state.isLoading shouldBe false
        state.games.size shouldBe 2
        state.error shouldBe null
    }

    @Test
    fun `fetchPopularGames failure should update uiState with error`() = runTest {
        // Arrange
        val errorMessage = "Network Error"
        coEvery { gameRepository.getPopularGames() } throws RuntimeException(errorMessage)
        gameViewModel = GameViewModel(gameRepository)
        
        // Assert
        val state = gameViewModel.uiState.value
        state.isLoading shouldBe false
        state.games.isEmpty() shouldBe true
        state.error shouldNotBe null
    }
}