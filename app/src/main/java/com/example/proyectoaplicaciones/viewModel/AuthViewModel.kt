package com.example.proyectoaplicaciones.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoaplicaciones.data.model.ErrorResponse
import com.example.proyectoaplicaciones.data.model.User
import com.example.proyectoaplicaciones.repository.AuthRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    val profileImageUri: Uri? = null, // Campo para la imagen de perfil
    val authError: String? = null,
    val isLoading: Boolean = false,
    val isAuthSuccessful: Boolean = false,
    val isAuthenticated: Boolean = false,
    val isGuest: Boolean = false,
    val user: User? = null
)

class AuthViewModel(private val authRepository: AuthRepository = AuthRepository()) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onProfileImageChange(uri: Uri?) {
        _uiState.update { it.copy(profileImageUri = uri) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, isEmailValid = "@" in email, authError = null) }
    }

    fun onPasswordChange(password: String) {
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasDigit = password.any { it.isDigit() }
        val isPasswordValid = password.length > 5 && hasUpperCase && hasDigit
        _uiState.update { it.copy(password = password, isPasswordValid = isPasswordValid, authError = null) }
    }

    fun onUsernameChange(username: String) {
        _uiState.update { it.copy(username = username, authError = null) }
    }

    fun login() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, authError = null) }
            try {
                val response = authRepository.login(_uiState.value.email, _uiState.value.password)
                if (response.isSuccessful) {
                    val user = response.body()
                    _uiState.update { it.copy(isLoading = false, isAuthSuccessful = true, user = user, isAuthenticated = true, email = _uiState.value.email) }
                } else {
                     val errorMessage = "Error ${response.code()}: ${response.message()}"
                    _uiState.update { it.copy(isLoading = false, authError = errorMessage) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, authError = "Error de red: ${e.message}") }
            }
        }
    }

    fun register() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, authError = null) }
            try {
                val response = authRepository.register(
                    email = _uiState.value.email,
                    password = _uiState.value.password,
                    username = _uiState.value.username
                )
                if (response.isSuccessful) {
                    val user = response.body()
                    _uiState.update { it.copy(isLoading = false, isAuthSuccessful = true, user = user, isAuthenticated = true, email = _uiState.value.email) }
                } else {
                    val errorMessage = "Error ${response.code()}: ${response.message()}"
                     _uiState.update { it.copy(isLoading = false, authError = errorMessage) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, authError = "Error de red: ${e.message}") }
            }
        }
    }

    fun logout() {
        // Al cerrar sesión, reseteamos todo excepto el estado de invitado
        _uiState.value = AuthUiState(isGuest = true)
    }

    fun setGuest() {
        _uiState.value = AuthUiState(isGuest = true)
    }

    fun onAuthSuccessNavigated() {
        _uiState.update { it.copy(isAuthSuccessful = false, authError = null) }
    }
}
