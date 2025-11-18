package com.example.proyectoaplicaciones.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoaplicaciones.Data.Model.User
import com.example.proyectoaplicaciones.Data.Remote.RetrofitInstance
import com.example.proyectoaplicaciones.Repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.Exception

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
    val user: User? = null
)

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()
    private val authRepository = AuthRepository(RetrofitInstance.api)

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
        val isPasswordValid = password.length > 6

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
            _uiState.update { it.copy(isLoading = true, authError = null) }
            try {
                val response = authRepository.login(_uiState.value.email, _uiState.value.password)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update { it.copy(isAuthSuccessful = true, isAuthenticated = true, user = response.body()) }
                } else {
                    _uiState.update { it.copy(authError = "Login failed: " + response.message()) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(authError = "Login failed: " + e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun register() {
        if (!validateFields() || _uiState.value.username.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, authError = null) }
            try {
                val response = authRepository.register(_uiState.value.email, _uiState.value.password, _uiState.value.username)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update { it.copy(isAuthSuccessful = true, isAuthenticated = true, user = response.body()) }
                } else {
                    _uiState.update { it.copy(authError = "Registration failed: " + response.message()) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(authError = "Registration failed: " + e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
    
    fun logout() {
        _uiState.value = AuthUiState()
    }
}
