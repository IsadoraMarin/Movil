package com.example.proyectoaplicaciones.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoaplicaciones.data.model.ErrorResponse
import com.example.proyectoaplicaciones.data.model.Role
import com.example.proyectoaplicaciones.data.model.User
import com.example.proyectoaplicaciones.data.remote.SessionManager
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
    val profileImageUri: Uri? = null,
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

    init {
        val loggedInUser = SessionManager.currentUser
        if (loggedInUser != null) {
            _uiState.update { it.copy(isAuthenticated = true, user = loggedInUser, email = loggedInUser.email) }
        }
    }

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
                val response = authRepository.login(email = _uiState.value.email, password = _uiState.value.password)
                if (response.isSuccessful) {
                    // --- INICIO DE LA CORRECCIÓN ---
                    val authResponse = response.body()
                    if (authResponse != null) {
                        // 1. Creamos un objeto User a partir de la respuesta del backend.
                        val user = User(
                            id = authResponse.id,
                            username = authResponse.username,
                            email = authResponse.email,
                            role = authResponse.role,
                            token = authResponse.token
                        )
                        // 2. Guardamos el usuario con el token en la sesión.
                        SessionManager.currentUser = user
                        // 3. Actualizamos la UI.
                        _uiState.update { it.copy(isLoading = false, isAuthSuccessful = true, user = user, isAuthenticated = true, email = user.email, isGuest = false) }
                    } else {
                        _uiState.update { it.copy(isLoading = false, authError = "Respuesta vacía del servidor") }
                    }
                    // --- FIN DE LA CORRECCIÓN ---
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        Gson().fromJson(errorBody, ErrorResponse::class.java).message
                    } catch (e: Exception) {
                        "Error ${response.code()}: ${response.message()}"
                    }
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
                    // --- INICIO DE LA CORRECCIÓN ---
                    val authResponse = response.body()
                    if (authResponse != null) {
                        val user = User(
                            id = authResponse.id,
                            username = authResponse.username,
                            email = authResponse.email,
                            role = authResponse.role,
                            token = authResponse.token
                        )
                        SessionManager.currentUser = user
                        _uiState.update { it.copy(isLoading = false, isAuthSuccessful = true, user = user, isAuthenticated = true, email = user.email, isGuest = false) }
                    } else {
                        _uiState.update { it.copy(isLoading = false, authError = "Respuesta vacía del servidor") }
                    }
                    // --- FIN DE LA CORRECCIÓN ---
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        Gson().fromJson(errorBody, ErrorResponse::class.java).message
                    } catch (e: Exception) {
                        "Error ${response.code()}: ${response.message()}"
                    }
                    _uiState.update { it.copy(isLoading = false, authError = errorMessage) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, authError = "Error de red: ${e.message}") }
            }
        }
    }

    fun logout() {
        SessionManager.currentUser = null
        _uiState.value = AuthUiState()
    }

    fun setGuest() {
        _uiState.update { AuthUiState(isGuest = true) }
    }

    fun grantAdminRole() {
        _uiState.update {
            val updatedUser = it.user?.copy(role = Role.ADMIN)
            SessionManager.currentUser = updatedUser
            it.copy(user = updatedUser)
        }
    }

    fun onAuthSuccessNavigated() {
        _uiState.update { it.copy(isAuthSuccessful = false, authError = null) }
    }
}