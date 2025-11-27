package com.example.proyectoaplicaciones.repository

import com.example.proyectoaplicaciones.Data.Model.Role
import com.example.proyectoaplicaciones.Data.Model.User
import com.example.proyectoaplicaciones.Data.Remote.ApiService
import com.example.proyectoaplicaciones.Repository.AuthRepository
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Response

class AuthRepositoryTest {

    private lateinit var apiService: ApiService
    private lateinit var authRepository: AuthRepository

    @BeforeEach
    fun setUp() {
        apiService = mockk()
        authRepository = AuthRepository(apiService)
    }

    @Test
    fun `login should return user on success`() = runTest {
        // Arrange
        val fakeUser = User(1, "testuser", Role.USER, "fake-token")
        coEvery { apiService.login(any()) } returns Response.success(fakeUser)

        // Act
        val result = authRepository.login("test@test.com", "password")

        // Assert
        result.isSuccessful shouldBe true
        result.body() shouldBe fakeUser
    }

    @Test
    fun `register should return user on success`() = runTest {
        // Arrange
        val fakeUser = User(1, "newuser", Role.USER, "new-fake-token")
        coEvery { apiService.register(any()) } returns Response.success(fakeUser)

        // Act
        val result = authRepository.register("new@test.com", "password", "newuser")

        // Assert
        result.isSuccessful shouldBe true
        result.body() shouldBe fakeUser
    }
}