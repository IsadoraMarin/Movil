package com.example.proyectoaplicaciones.viewmodel

import com.example.proyectoaplicaciones.Data.Model.Post
import com.example.proyectoaplicaciones.Repository.PostRepository
import com.example.proyectoaplicaciones.rules.InstantExecutorExtension
import com.example.proyectoaplicaciones.rules.MainDispatcherRule
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension

@ExperimentalCoroutinesApi
@ExtendWith(InstantExecutorExtension::class)
class PostViewModelTest {

    @JvmField
    @RegisterExtension
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var postRepository: PostRepository
    private lateinit var postViewModel: PostViewModel

    private val dummyPosts = listOf(
        Post(1, 1, "Title 1", "Body 1", 10),
        Post(2, 2, "Title 2", "Body 2", 20),
        Post(100, 3, "Title 100", "Body 100", 5)
    )

    @BeforeEach
    fun setUp() {
        postRepository = mockk()
        coEvery { postRepository.getPosts() } returns dummyPosts
        postViewModel = PostViewModel(postRepository)
    }

    @Test
    fun `fetchPosts should categorize posts correctly`() = runTest {
        // Assert
        postViewModel.newsPosts.value.size shouldBe 2
        postViewModel.popularPosts.value.size shouldBe 3
        postViewModel.communityPosts.value.size shouldBe 1
        postViewModel.popularPosts.value.first().score shouldBe 20
    }

    @Test
    fun `handleVote should update post score`() = runTest {
        // Act
        postViewModel.handleVote(1, VoteType.LIKE)

        // Assert
        val post = postViewModel.newsPosts.value.find { it.id == 1 }
        post?.score shouldBe 11
        postViewModel.userVotes.value[1] shouldBe VoteType.LIKE
    }

    @Test
    fun `toggleFavorite should add and remove post from favorites`() = runTest {
        val post = dummyPosts.first()

        // Add to favorites
        postViewModel.toggleFavorite(post)
        postViewModel.favoritePosts.value shouldContain post

        // Remove from favorites
        postViewModel.toggleFavorite(post)
        postViewModel.favoritePosts.value.isEmpty() shouldBe true
    }
}