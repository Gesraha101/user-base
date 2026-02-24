package com.spark.userbase.feature.list_users.presentation.viewmodel

import com.google.common.truth.Truth.assertThat
import com.spark.userbase.feature.add_user.domain.model.User
import com.spark.userbase.feature.add_user.domain.usecase.GetUsersUseCase
import com.spark.userbase.feature.add_user.presentation.viewmodel.UnconfinedDispatcherRule
import com.spark.userbase.feature.list_users.presentation.viewmodel.state.ListUsersUiState
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ListUsersViewModelTest {

    @MockK lateinit var getUsers: GetUsersUseCase

    @get:Rule
    val dispatcherRule = UnconfinedDispatcherRule()

    private val usersFlow = MutableSharedFlow<Result<List<User>>>()
    private lateinit var viewModel: ListUsersViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { getUsers() } returns usersFlow
        viewModel = ListUsersViewModel(getUsers)
    }

    // region Initial state

    @Test
    fun `initial state is Loading`() = runTest {
        assertThat(viewModel.state.value).isEqualTo(ListUsersUiState.Loading)
    }

    // endregion

    // region Success

    @Test
    fun `state is Success with users when use case emits success`() = runTest {
        val users = listOf(User(id = 1, name = "Alice", age = 25, jobTitle = "Engineer", gender = "Female"))
        val collected = mutableListOf<ListUsersUiState>()
        val collectJob = launch { viewModel.state.collect { collected.add(it) } }

        usersFlow.emit(Result.success(users))
        advanceUntilIdle()

        val successStates = collected.filterIsInstance<ListUsersUiState.Success>()
        assertThat(successStates).isNotEmpty()
        assertThat(successStates.last().users).isEqualTo(users)
        collectJob.cancel()
    }

    @Test
    fun `state is Success with empty list when use case emits empty success`() = runTest {
        val collected = mutableListOf<ListUsersUiState>()
        val collectJob = launch { viewModel.state.collect { collected.add(it) } }

        usersFlow.emit(Result.success(emptyList()))
        advanceUntilIdle()

        val successStates = collected.filterIsInstance<ListUsersUiState.Success>()
        assertThat(successStates).isNotEmpty()
        assertThat(successStates.last().users).isEmpty()
        collectJob.cancel()
    }

    @Test
    fun `state reflects most recent users on multiple success emissions`() = runTest {
        val firstUsers = listOf(User(id = 1, name = "Alice", age = 25, jobTitle = "Engineer", gender = "Female"))
        val secondUsers = listOf(
            User(id = 1, name = "Alice", age = 25, jobTitle = "Engineer", gender = "Female"),
            User(id = 2, name = "Bob", age = 30, jobTitle = "Designer", gender = "Male"),
        )
        val collected = mutableListOf<ListUsersUiState>()
        val collectJob = launch { viewModel.state.collect { collected.add(it) } }

        usersFlow.emit(Result.success(firstUsers))
        usersFlow.emit(Result.success(secondUsers))
        advanceUntilIdle()

        assertThat((collected.last() as ListUsersUiState.Success).users).isEqualTo(secondUsers)
        collectJob.cancel()
    }

    // endregion

    // region Error

    @Test
    fun `state is Error when use case emits failure`() = runTest {
        val collected = mutableListOf<ListUsersUiState>()
        val collectJob = launch { viewModel.state.collect { collected.add(it) } }

        usersFlow.emit(Result.failure(RuntimeException("DB error")))
        advanceUntilIdle()

        val errorStates = collected.filterIsInstance<ListUsersUiState.Error>()
        assertThat(errorStates).isNotEmpty()
        assertThat(errorStates.last().value).isEqualTo("DB error")
        collectJob.cancel()
    }

    // endregion

    // region State transitions

    @Test
    fun `state transitions from Loading to Success after first emission`() = runTest {
        val users = listOf(User(id = 1, name = "Bob", age = 30, jobTitle = "Designer", gender = "Male"))
        val collected = mutableListOf<ListUsersUiState>()
        val collectJob = launch { viewModel.state.collect { collected.add(it) } }

        // small delay for collection propagation
        advanceTimeBy(100)

        usersFlow.emit(Result.success(users))
        advanceUntilIdle()

        assertThat(collected.first()).isEqualTo(ListUsersUiState.Loading)
        assertThat((collected.last() as ListUsersUiState.Success).users).isEqualTo(users)
        collectJob.cancel()
    }

    @Test
    fun `state transitions from Loading to Error after first failure`() = runTest {
        val collected = mutableListOf<ListUsersUiState>()
        val collectJob = launch { viewModel.state.collect { collected.add(it) } }

        // small delay for collection propagation
        advanceTimeBy(100)

        usersFlow.emit(Result.failure(RuntimeException("error")))
        advanceUntilIdle()

        assertThat(collected.first()).isEqualTo(ListUsersUiState.Loading)
        assertThat((collected.last() as ListUsersUiState.Error).value).isEqualTo("error")
        collectJob.cancel()
    }

    @Test
    fun `state transitions from Success to Error on subsequent failure`() = runTest {
        val users = listOf(User(id = 1, name = "Carol", age = 22, jobTitle = "Manager", gender = "Female"))
        val collected = mutableListOf<ListUsersUiState>()
        val collectJob = launch { viewModel.state.collect { collected.add(it) } }

        // small delay for collection propagation
        advanceTimeBy(100)

        usersFlow.emit(Result.success(users))

        // small delay for collection propagation
        advanceTimeBy(100)

        usersFlow.emit(Result.failure(RuntimeException("error")))
        advanceUntilIdle()

        assertThat(collected).hasSize(3)
        assertThat(collected[0]).isEqualTo(ListUsersUiState.Loading)
        assertThat((collected[1] as ListUsersUiState.Success).users).isEqualTo(users)
        assertThat((collected[2] as ListUsersUiState.Error).value).isEqualTo("error")
        collectJob.cancel()
    }

    @Test
    fun `state transitions from Error to Success on subsequent success`() = runTest {
        val users = listOf(User(id = 1, name = "Dave", age = 28, jobTitle = "Analyst", gender = "Male"))
        val collected = mutableListOf<ListUsersUiState>()
        val collectJob = launch { viewModel.state.collect { collected.add(it) } }

        // small delay for collection propagation
        advanceTimeBy(100)

        usersFlow.emit(Result.failure(RuntimeException("error")))

        // small delay for collection propagation
        advanceTimeBy(100)

        usersFlow.emit(Result.success(users))
        advanceUntilIdle()

        assertThat(collected).hasSize(3)
        assertThat(collected[0]).isEqualTo(ListUsersUiState.Loading)
        assertThat((collected[1] as ListUsersUiState.Error).value).isEqualTo("error")
        assertThat((collected[2] as ListUsersUiState.Success).users).isEqualTo(users)
        collectJob.cancel()
    }

    // endregion
}