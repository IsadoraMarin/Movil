package com.example.proyectoaplicaciones.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoaplicaciones.data.model.Article
import com.example.proyectoaplicaciones.repository.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewsViewModel(private val newsRepository: NewsRepository = NewsRepository()) : ViewModel() {

    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun fetchNews() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val news = newsRepository.getGamingNews()
                _articles.value = news
            } catch (e: Exception) {
                _articles.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}