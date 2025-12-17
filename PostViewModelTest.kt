package com.example.proyectoaplicaciones.viewmodel

import com.example.proyectoaplicaciones.Data.Model.Post
import com.example.proyectoaplicaciones.Repository.PostRepository
import com.example.proyectoaplicaciones.rules.InstantExecutorExtension
import com.example.proyectoaplicaciones.rules.MainDispatcherRule
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import java.lang.RuntimeException

@ExperimentalCoroutinesApi
@ExtendWith(InstantExecutorExtension::class)
class PostViewModelTest {

    @JvmField
    @RegisterExtension
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var postRepository: PostRepository
    private lateinit var postViewModel: PostViewModel

    @BeforeEach
    fun setUp() {
        postRepository = mockk(relaxed = true)
    }

    @Test
    fun `fetchPosts success should update post lists`() {
        // Arrange
        val fakePosts = listOf(Post(1, 1, "Noticia", "...", 10), Post(100, 2, "Comunidad", "...", 20))
        coEvery { postRepository.getPosts() } returns fakePosts

        // Act
        postViewModel = PostViewModel(postRepository)

        // Assert
        postViewModel.newsPosts.value.shouldHaveSize(1)
        postViewModel.communityPosts.value.shouldHaveSize(1)
    }

    @Test
    fun `fetchPosts failure should keep lists empty`() {
        // Arrange
        coEvery { postRepository.getPosts() } throws RuntimeException("Network Error")

        // Act
        postViewModel = PostViewModel(postRepository)

        // Assert
        postViewModel.newsPosts.value.shouldBeEmpty()
        postViewModel.communityPosts.value.shouldBeEmpty()
    }

    @Test
    fun `deletePost should call repository and refresh list`() {
        // Arrange (definimos una secuencia de respuestas)
        val initialPost = Post(100, 1, "Post para borrar", "...", 10)
        coEvery { postRepository.getPosts() } returns listOf(initialPost) andThen emptyList()

        // Act (Init)
        postViewModel = PostViewModel(postRepository)
        postViewModel.communityPosts.value.shouldHaveSize(1) // Pre-condición

        // Act (Delete)
        postViewModel.deletePost(100)

        // Assert
        coVerify(exactly = 1) { postRepository.deletePost(100) }
        coVerify(exactly = 2) { postRepository.getPosts() }
        postViewModel.communityPosts.value.shouldBeEmpty()
    }

    @Test
    fun `publishPost should call repository and refresh list`() {
        // Arrange
        val newPost = Post(101, 123, "Nuevo Post", "...", 0)
        coEvery { postRepository.getPosts() } returns emptyList() andThen listOf(newPost)

        // Act (Init)
        postViewModel = PostViewModel(postRepository)
        postViewModel.communityPosts.value.shouldBeEmpty() // Pre-condición

        // Act (Publish)
        postViewModel.publishPost("Nuevo Post", "...", 123)

        // Assert
        coVerify(exactly = 1) { postRepository.addPost(any()) }
        coVerify(exactly = 2) { postRepository.getPosts() }
        postViewModel.communityPosts.value.shouldHaveSize(1)
    }
}