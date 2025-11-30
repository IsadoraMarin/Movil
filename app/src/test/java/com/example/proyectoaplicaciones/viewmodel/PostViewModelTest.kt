package com.example.proyectoaplicaciones.viewmodel

import app.cash.turbine.test
import com.example.proyectoaplicaciones.data.model.Comentarios
import com.example.proyectoaplicaciones.data.model.Post
import com.example.proyectoaplicaciones.repository.PostRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
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
import com.example.proyectoaplicaciones.viewModel.PostViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class PostViewModelTest {

    private lateinit var viewModel: PostViewModel
    private lateinit var mockPostRepository: PostRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockPostRepository = mockk(relaxed = true)
        viewModel = PostViewModel(postRepository = mockPostRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cuando fetchPosts es exitoso, los posts se cargan y ordenan`() = runTest {
        val fakePosts = listOf(
            Post(userId = 1, id = 1, title = "Post C", body = "Body C", score = 10),
            Post(userId = 2, id = 3, title = "Post A", body = "Body A", score = 30),
            Post(userId = 3, id = 2, title = "Post B", body = "Body B", score = 20)
        )
        coEvery { mockPostRepository.getPosts() } returns fakePosts

        viewModel.communityPosts.test {
            assertThat(awaitItem()).isEmpty()

            viewModel.fetchPosts()

            assertThat(awaitItem().map { it.id }).containsExactly(3, 2, 1).inOrder()
        }

        viewModel.popularPosts.test {
            assertThat(awaitItem().map { it.score }).containsExactly(30, 20, 10).inOrder()
        }
    }

    @Test
    fun `cuando fetchPosts falla, el estado de error se actualiza`() = runTest {
        val errorMessage = "Error de red"
        coEvery { mockPostRepository.getPosts() } throws RuntimeException(errorMessage)

        viewModel.error.test {
            assertThat(awaitItem()).isNull()
            viewModel.fetchPosts()
            assertThat(awaitItem()).contains(errorMessage)
        }
    }

    @Test
    fun `cuando selectPost es llamado, selectedPost y comments se actualizan`() = runTest {
        // Arrange
        val postToSelect = Post(userId = 1, id = 123, title = "Selected Post", body = "Selected Body", score = 100)
        val fakeComments = listOf(Comentarios(postId = 123, id = 1, name = "Author", body = "Comment body"))
        coEvery { mockPostRepository.getComments(123) } returns fakeComments

        viewModel.comments.test {
            assertThat(awaitItem()).isEmpty()

            // Act
            viewModel.selectPost(postToSelect)

            // Assert
            assertThat(viewModel.selectedPost.value).isEqualTo(postToSelect)
            assertThat(awaitItem()).isEqualTo(fakeComments)
        }

        // Verificamos que la llamada al repositorio se hizo como se esperaba
        coVerify { mockPostRepository.getComments(postId = 123) }
    }
}
