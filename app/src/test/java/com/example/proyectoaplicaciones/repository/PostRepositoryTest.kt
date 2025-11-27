package com.example.proyectoaplicaciones.repository

import com.example.proyectoaplicaciones.Data.Model.Post
import com.example.proyectoaplicaciones.Data.Remote.ApiService
import com.example.proyectoaplicaciones.Repository.PostRepository
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PostRepositoryTest {

    private lateinit var apiService: ApiService
    private lateinit var postRepository: PostRepository

    @BeforeEach
    fun setUp() {
        apiService = mockk()
        postRepository = PostRepository(apiService)
    }

    @Test
    fun `getPosts should return list of posts`() = runTest {
        // Arrange
        val dummyPosts = listOf(Post(1, 1, "Title 1", "Body 1", 10))
        coEvery { apiService.getPosts() } returns dummyPosts

        // Act
        val result = postRepository.getPosts()

        // Assert
        result shouldBe dummyPosts
    }

    @Test
    fun `addPost should call apiService's addPost`() = runTest {
        // Arrange
        val newPost = Post(0, 1, "New Title", "New Body", 0)
        coEvery { apiService.addPost(any()) } returns Unit

        // Act
        postRepository.addPost(newPost)

        // Assert
        coVerify { apiService.addPost(newPost) }
    }
}