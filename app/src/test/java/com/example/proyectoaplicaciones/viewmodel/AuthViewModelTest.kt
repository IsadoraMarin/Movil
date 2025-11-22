package com.example.proyectoaplicaciones.viewmodel

import com.example.proyectoaplicaciones.Data.Model.Role
import com.example.proyectoaplicaciones.Data.Model.User
import com.example.proyectoaplicaciones.Repository.AuthRepository
import com.example.proyectoaplicaciones.rules.InstantExecutorExtension
import com.example.proyectoaplicaciones.rules.MainDispatcherRule
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import retrofit2.Response

@ExperimentalCoroutinesApi
@ExtendWith(InstantExecutorExtension::class) // <- Usamos la nueva extensión de JUnit 5
class AuthViewModelTest {

    @JvmField
    @RegisterExtension
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var authRepository: AuthRepository
    private lateinit var authViewModel: AuthViewModel

    @BeforeEach
    fun setUp() {
        authRepository = mockk()
        authViewModel = AuthViewModel(authRepository)
    }

    @Test
    fun `login success should update uiState correctly`() {
        // Arrange
        val fakeUser = User(1, "testuser", Role.USER, "fake-token")
        coEvery { authRepository.login(any(), any()) } returns Response.success(fakeUser)

        // Act
        authViewModel.onEmailChange("test@test.com")
        authViewModel.onPasswordChange("Password123")
        authViewModel.login()

        // Assert
        val state = authViewModel.uiState.value
        state.isAuthenticated shouldBe true
        state.isAuthSuccessful shouldBe true
        state.user shouldBe fakeUser
        state.isLoading shouldBe false
        state.authError shouldBe null
    }

    @Test
    fun `login failure should update uiState with error`() {
        // Arrange
        val errorMessage = "Unauthorized"
        coEvery { authRepository.login(any(), any()) } returns Response.error(401, errorMessage.toResponseBody())

        // Act
        authViewModel.onEmailChange("wrong@test.com")
        authViewModel.onPasswordChange("wrongpassword")
        authViewModel.login()

        // Assert
        val state = authViewModel.uiState.value
        state.isAuthenticated shouldBe false
        state.isAuthSuccessful shouldBe false
        state.user shouldBe null
        state.isLoading shouldBe false
        state.authError shouldNotBe null
    }
}