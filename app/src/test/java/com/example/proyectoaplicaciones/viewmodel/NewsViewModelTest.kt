package com.example.proyectoaplicaciones.viewmodel

import app.cash.turbine.test
import com.example.proyectoaplicaciones.data.model.Article
import com.example.proyectoaplicaciones.data.model.Source
import com.example.proyectoaplicaciones.repository.NewsRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
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

// Se importa el ViewModel que se va a probar
import com.example.proyectoaplicaciones.viewModel.NewsViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class NewsViewModelTest {

    private lateinit var viewModel: NewsViewModel
    private lateinit var mockNewsRepository: NewsRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockNewsRepository = mockk()
        viewModel = NewsViewModel(newsRepository = mockNewsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cuando fetchNews es exitoso, el stateflow de articles se actualiza`() = runTest {
        // Arrange
        val fakeArticles = listOf(
            Article("Title 1", "Desc 1", "url1", "img1", "date1", Source("Src1")),
            Article("Title 2", "Desc 2", "url2", "img2", "date2", Source("Src2"))
        )
        coEvery { mockNewsRepository.getGamingNews() } returns fakeArticles

        // Act & Assert
        viewModel.articles.test {
            // El estado inicial es una lista vacía
            assertThat(awaitItem()).isEmpty()

            // Llamamos a la función a probar
            viewModel.fetchNews()

            // Esperamos a que los artículos se emitan y comprobamos el resultado
            val loadedArticles = awaitItem()
            assertThat(loadedArticles).isEqualTo(fakeArticles)
            assertThat(loadedArticles).hasSize(2)
        }
    }
}
