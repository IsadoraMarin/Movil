package com.example.proyectoaplicaciones.repository

import com.example.proyectoaplicaciones.Data.Model.ExternalGame
import com.example.proyectoaplicaciones.Data.Model.GameListResponse
import com.example.proyectoaplicaciones.Data.Remote.ExternalApiService
import com.example.proyectoaplicaciones.Repository.GameRepository
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GameRepositoryTest {

    private lateinit var externalApiService: ExternalApiService
    private lateinit var gameRepository: GameRepository

    @BeforeEach
    fun setUp() {
        externalApiService = mockk()
        gameRepository = GameRepository(externalApiService)
    }

    @Test
    fun `getPopularGames should return game list response`() = runTest {
        // Arrange
        val dummyGames = listOf(ExternalGame(id = 1, name = "Game 1", released = "2023-01-01", backgroundImage = "background1", rating = 4.5))
        val dummyResponse = GameListResponse(results = dummyGames)
        coEvery { externalApiService.getPopularGames(any()) } returns dummyResponse

        // Act
        val result = gameRepository.getPopularGames()

        // Assert
        result shouldBe dummyResponse
    }
}