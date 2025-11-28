package com.example.proyectoaplicaciones.viewmodel

import app.cash.turbine.test
import com.example.proyectoaplicaciones.data.model.Game
import com.example.proyectoaplicaciones.repository.GameRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import com.example.proyectoaplicaciones.viewModel.GameViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    private lateinit var viewModel: GameViewModel
    private lateinit var mockGameRepository: GameRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockGameRepository = mockk()
        viewModel = GameViewModel(repository = mockGameRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cuando fetchPopularGames es exitoso, uiState se actualiza con juegos`() = runTest {
        // Arrange
        val fakeGames = listOf(Game(id = 1, slug = "witcher-3", name = "The Witcher 3", background_image = "url"))
        coEvery { mockGameRepository.getPopularGames() } returns fakeGames

        // Act & Assert
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertThat(initialState.games).isEmpty()
            assertThat(initialState.isLoading).isFalse()

            viewModel.fetchPopularGames()

            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()

            val successState = awaitItem()
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.games).isEqualTo(fakeGames)
            assertThat(successState.error).isNull()
        }
    }

    @Test
    fun `cuando fetchPopularGames falla, uiState se actualiza con error`() = runTest {
        // Arrange
        val errorMessage = "Network Error"
        coEvery { mockGameRepository.getPopularGames() } throws RuntimeException(errorMessage)

        // Act & Assert
        viewModel.uiState.test {
            awaitItem() // Ignoramos el estado inicial

            viewModel.fetchPopularGames()

            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()

            val errorState = awaitItem()
            assertThat(errorState.isLoading).isFalse()
            assertThat(errorState.games).isEmpty()
            assertThat(errorState.error).contains(errorMessage)
        }
    }
}
