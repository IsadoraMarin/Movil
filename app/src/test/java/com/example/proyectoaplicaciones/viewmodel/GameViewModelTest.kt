package com.example.proyectoaplicaciones.viewmodel

import com.example.proyectoaplicaciones.Data.Model.ExternalGame
import com.example.proyectoaplicaciones.Data.Model.GameListResponse
import com.example.proyectoaplicaciones.Repository.GameRepository
import com.example.proyectoaplicaciones.rules.InstantExecutorExtension
import com.example.proyectoaplicaciones.rules.MainDispatcherRule
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import java.lang.RuntimeException

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
    fun `fetchPopularGames success should update uiState with games`() {
        // Arrange
        val fakeGames = listOf(ExternalGame(1, "Game 1", "2023-01-01", "image1.jpg", 4.5))
        val gameListResponse = GameListResponse(results = fakeGames)
        coEvery { gameRepository.getPopularGames() } returns gameListResponse

        // Act
        gameViewModel = GameViewModel(gameRepository)

        // Assert
        val state = gameViewModel.uiState.value
        state.isLoading shouldBe false
        state.games shouldBe fakeGames
        state.error shouldBe null
    }

    @Test
    fun `fetchPopularGames failure should update uiState with error`() {
        // Arrange
        val errorMessage = "Network Error"
        coEvery { gameRepository.getPopularGames() } throws RuntimeException(errorMessage)

        // Act
        gameViewModel = GameViewModel(gameRepository)

        // Assert
        val state = gameViewModel.uiState.value
        state.isLoading shouldBe false
        state.games.shouldBeEmpty()
        state.error shouldNotBe null
    }
}