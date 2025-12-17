package com.example.proyectoaplicaciones.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoaplicaciones.data.model.Article
import com.example.proyectoaplicaciones.repository.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NewsViewModel(private val newsRepository: NewsRepository = NewsRepository()) : ViewModel() {

    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // --- INICIO DE LA CORRECCIÓN: MANEJO DE ERRORES ---
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    // --- FIN DE LA CORRECCIÓN ---

    fun fetchNews() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null // Limpiar errores anteriores
            try {
                val news = newsRepository.getGamingNews()
                _articles.value = news
            } catch (e: Exception) {
                // --- INICIO DE LA CORRECCIÓN: Notificar al UI sobre el error ---
                _error.value = "Error al cargar las noticias: ${e.message}"
                _articles.value = emptyList() // Asegurarse de que la lista esté vacía en caso de error
                // --- FIN DE LA CORRECCIÓN ---
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- INICIO DE LA CORRECCIÓN: Función para limpiar el error ---
    fun clearError() {
        _error.value = null
    }
    // --- FIN DE LA CORRECCIÓN ---
}
