package com.example.proyectoaplicaciones.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val isAuthenticated: Boolean = false, // Estado persistente de autenticación
    val isGuest: Boolean = false,
    val isModerator: Boolean = false // Nuevo estado para el rol de moderador
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
            val username = _uiState.value.username
            val isModerator = username.equals("admin", ignoreCase = true)
            _uiState.update { it.copy(isLoading = false, isAuthSuccessful = true, isAuthenticated = true, isGuest = false, isModerator = isModerator) }
        }
    }

    fun register() {
        if (!validateFields() || _uiState.value.username.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(2000)
            val username = _uiState.value.username
            val isModerator = username.equals("admin", ignoreCase = true)
            _uiState.update { it.copy(isLoading = false, isAuthSuccessful = true, isAuthenticated = true, isGuest = false, isModerator = isModerator) }
        }
    }
    
    fun logout() {
        _uiState.value = AuthUiState()
    }
}
