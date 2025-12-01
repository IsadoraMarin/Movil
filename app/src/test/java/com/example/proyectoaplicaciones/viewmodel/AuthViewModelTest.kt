package com.example.proyectoaplicaciones.viewmodel

import app.cash.turbine.test
import com.example.proyectoaplicaciones.repository.AuthRepository
import com.google.common.truth.Truth.assertThat
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
import com.example.proyectoaplicaciones.viewModel.AuthViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private lateinit var viewModel: AuthViewModel
    private lateinit var mockAuthRepository: AuthRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockAuthRepository = mockk(relaxed = true)
        viewModel = AuthViewModel(authRepository = mockAuthRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cuando onEmailChange es llamado, el estado del email se actualiza`() = runTest {
        val nuevoEmail = "test@email.com"

        viewModel.uiState.test {
            // Estado inicial
            val estadoInicial = awaitItem()
            assertThat(estadoInicial.email).isEmpty()

            // Acción
            viewModel.onEmailChange(nuevoEmail)

            // Comprobación
            val estadoActualizado = awaitItem()
            assertThat(estadoActualizado.email).isEqualTo(nuevoEmail)
            assertThat(estadoActualizado.isEmailValid).isTrue()
        }
    }
}
