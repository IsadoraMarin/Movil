package com.example.proyectoaplicaciones.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoaplicaciones.Data.Model.Role
import com.example.proyectoaplicaciones.Data.Model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val email: String = "",
    val isEmailValid: Boolean = true,
    val password: String = "",
    val isPasswordValid: Boolean = true,
    val username: String = "",
    val phone: String = "",
    val authError: String? = null,
    val isLoading: Boolean = false,
    val isAuthSuccessful: Boolean = false, // Evento para navegar
    val isAuthenticated: Boolean = false, // Estado persistente de autenticaciÃ³n
    val isGuest: Boolean = false,
    val user: User? = null
)

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, isEmailValid = true, authError = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, isPasswordValid = true, authError = null) }
    }

    fun onUsernameChange(username: String) {
        _uiState.update { it.copy(username = username) }
    }

    fun onPhoneChange(phone: String) {
        _uiState.update { it.copy(phone = phone) }
    }

    fun onAuthSuccessNavigated() {
        _uiState.update { it.copy(isAuthSuccessful = false, authError = null) }
    }

    fun setGuest() {
        _uiState.update { it.copy(isGuest = true, isAuthenticated = false, isAuthSuccessful = true) }
    }

    private fun validateFields(): Boolean {
        val email = _uiState.value.email
        val password = _uiState.value.password

        val isEmailValid = "@" in email
        val isPasswordValid = password.any { it.isUpperCase() } && password.any { it.isDigit() }

        _uiState.update {
            it.copy(
                isEmailValid = isEmailValid,
                isPasswordValid = isPasswordValid
            )
        }

        return isEmailValid && isPasswordValid
    }

    fun login() {
        if (!validateFields()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(1500)
            // Respuesta de Backend ¡Posiblemente funcione mejor con SpringBoot!
            val role = if (_uiState.value.username.equals("admin", ignoreCase = true)) Role.MODERATOR else Role.USER
            val user = User(1, _uiState.value.username, role, "fake-token")
            _uiState.update { it.copy(isLoading = false, isAuthSuccessful = true, isAuthenticated = true, isGuest = false, user = user) }
        }
    }

    fun register() {
        if (!validateFields() || _uiState.value.username.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(2000)
            //Otra simulacion de Backend
            val user = User(1, _uiState.value.username, Role.USER, "fake-token")
            _uiState.update { it.copy(isLoading = false, isAuthSuccessful = true, isAuthenticated = true, isGuest = false, user = user) }
        }
    }
    
    fun logout() {
        _uiState.value = AuthUiState()
    }
}
