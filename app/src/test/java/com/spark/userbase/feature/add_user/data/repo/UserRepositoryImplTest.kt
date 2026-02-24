package com.spark.userbase.feature.add_user.data.repo

import com.google.common.truth.Truth.assertThat
import com.spark.userbase.feature.add_user.data.model.UserEntity
import com.spark.userbase.feature.add_user.data.repo.source.AddUserLocalDataSource
import com.spark.userbase.feature.add_user.domain.model.User
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class UserRepositoryImplTest {

    @MockK lateinit var localDataSource: AddUserLocalDataSource

    private lateinit var repository: UserRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = UserRepositoryImpl(localDataSource)
    }

    // region addUser

    @Test
    fun `addUser returns success when data source inserts successfully`() = runTest {
        val user = User(name = "John Doe", age = 25, jobTitle = "Developer", gender = "Male")
        coEvery { localDataSource.insertUser(any()) } returns Unit

        val result = repository.addUser(user)

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `addUser passes correctly mapped entity to data source`() = runTest {
        val user = User(id = 0, name = "John Doe", age = 25, jobTitle = "Developer", gender = "Male")
        val expectedEntity = UserEntity(id = 0, name = "John Doe", age = 25, jobTitle = "Developer", gender = "Male")
        coEvery { localDataSource.insertUser(expectedEntity) } returns Unit

        repository.addUser(user)

        coVerify { localDataSource.insertUser(expectedEntity) }
    }

    @Test
    fun `addUser returns failure when data source throws`() = runTest {
        val user = User(name = "John Doe", age = 25, jobTitle = "Developer", gender = "Male")
        val exception = RuntimeException("DB error")
        coEvery { localDataSource.insertUser(any()) } throws exception

        val result = repository.addUser(user)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }

    // endregion

    // region getUsers

    @Test
    fun `getUsers emits success with mapped domain models`() = runTest {
        val entity = UserEntity(id = 1, name = "Jane", age = 30, jobTitle = "Designer", gender = "Female")
        val expectedUser = User(id = 1, name = "Jane", age = 30, jobTitle = "Designer", gender = "Female")
        every { localDataSource.getAllUsers() } returns flowOf(listOf(entity))

        val result = repository.getUsers().first()

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).containsExactly(expectedUser)
    }

    @Test
    fun `getUsers emits success with empty list when data source has no users`() = runTest {
        every { localDataSource.getAllUsers() } returns flowOf(emptyList())

        val result = repository.getUsers().first()

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEmpty()
    }

    @Test
    fun `getUsers emits failure when data source flow throws`() = runTest {
        val exception = RuntimeException("Flow error")
        every { localDataSource.getAllUsers() } returns flow { throw exception }

        val result = repository.getUsers().first()

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }

    @Test
    fun `getUsers maps multiple entities to domain models`() = runTest {
        val entities = listOf(
            UserEntity(id = 1, name = "Alice", age = 28, jobTitle = "Engineer", gender = "Female"),
            UserEntity(id = 2, name = "Bob", age = 35, jobTitle = "Manager", gender = "Male"),
        )
        every { localDataSource.getAllUsers() } returns flowOf(entities)

        val result = repository.getUsers().first()

        val users = result.getOrNull()
        assertThat(users).hasSize(2)
        assertThat(users?.map { it.name }).containsExactly("Alice", "Bob").inOrder()
    }

    // endregion
}
