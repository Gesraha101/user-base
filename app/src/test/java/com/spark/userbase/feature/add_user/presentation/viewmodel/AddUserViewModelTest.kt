package com.spark.userbase.feature.add_user.presentation.viewmodel

import com.google.common.truth.Truth.assertThat
import com.spark.userbase.common.ui.util.StringResource
import com.spark.userbase.common.ui.util.StringUnwrapper
import com.spark.userbase.feature.add_user.domain.model.User
import com.spark.userbase.feature.add_user.domain.usecase.AddUserUseCase
import com.spark.userbase.feature.add_user.presentation.viewmodel.event.AddUserEffect
import com.spark.userbase.feature.add_user.presentation.viewmodel.state.AddUserUiState
import com.spark.userbase.feature.add_user.presentation.viewmodel.state.InputFieldState
import com.spark.userbase.feature.add_user.presentation.viewmodel.stateholder.AddUserStateHolder
import com.spark.userbase.feature.add_user.presentation.viewmodel.stateholder.event.AddUserStateHolderEvent
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class AddUserViewModelTest {

    @MockK lateinit var addUserUseCase: AddUserUseCase
    @MockK lateinit var stateHolder: AddUserStateHolder
    @MockK lateinit var unwrapper: StringUnwrapper

    @get:Rule
    val dispatcherRule = UnconfinedDispatcherRule()
    private val eventChannel = Channel<AddUserStateHolderEvent>(Channel.BUFFERED)
    private lateinit var viewModel: AddUserViewModel

    private val defaultForm = AddUserUiState.Form(
        genderOptions = listOf("Male", "Female"),
        name = InputFieldState(onChanged = {}, onFocusLost = {}),
        age = InputFieldState(onChanged = {}, onFocusLost = {}),
        jobTitle = InputFieldState(onChanged = {}, onFocusLost = {}),
        gender = InputFieldState(onChanged = {}, onFocusLost = {}),
        onSubmit = {}
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { stateHolder.events } returns eventChannel.receiveAsFlow()
        every { stateHolder.state } returns MutableStateFlow(defaultForm)
        every { stateHolder.onNameChanged(any(), any()) } just runs
        every { stateHolder.onAgeChanged(any(), any()) } just runs
        every { stateHolder.onJobChanged(any(), any()) } just runs
        every { stateHolder.onGenderChanged(any(), any()) } just runs
        every { stateHolder.setSubmitting(any()) } just runs
        every { stateHolder.reset() } just runs
        viewModel = AddUserViewModel(addUserUseCase, stateHolder, unwrapper)
    }

    @After
    fun tearDown() {
        eventChannel.close()
    }

    // region Name validation

    @Test
    fun `OnNameChanged with one char calls onNameChanged with TOO_SHORT error`() = runTest {
        every { unwrapper.unwrap(any<StringResource>()) } returns "Name too short"

        eventChannel.send(AddUserStateHolderEvent.OnNameChanged("A"))

        coVerify { stateHolder.onNameChanged("A", "Name too short") }
    }

    @Test
    fun `OnNameChanged with exactly two chars calls onNameChanged with TOO_SHORT error`() = runTest {
        every { unwrapper.unwrap(any<StringResource>()) } returns "Name too short"

        eventChannel.send(AddUserStateHolderEvent.OnNameChanged("AB"))

        coVerify { stateHolder.onNameChanged("AB", "Name too short") }
    }

    @Test
    fun `OnNameChanged with valid name calls onNameChanged with null error`() = runTest {
        every { unwrapper.unwrap(null as StringResource?) } returns null

        eventChannel.send(AddUserStateHolderEvent.OnNameChanged("John Doe"))

        coVerify { stateHolder.onNameChanged("John Doe", null) }
    }

    @Test
    fun `OnNameChanged with digits calls onNameChanged with INVALID_CHARACTERS error`() = runTest {
        every { unwrapper.unwrap(any<StringResource>()) } returns "Invalid characters"

        eventChannel.send(AddUserStateHolderEvent.OnNameChanged("John1"))

        coVerify { stateHolder.onNameChanged("John1", "Invalid characters") }
    }

    @Test
    fun `OnNameChanged with special chars calls onNameChanged with INVALID_CHARACTERS error`() = runTest {
        every { unwrapper.unwrap(any<StringResource>()) } returns "Invalid characters"

        eventChannel.send(AddUserStateHolderEvent.OnNameChanged("John@"))

        coVerify { stateHolder.onNameChanged("John@", "Invalid characters") }
    }

    // endregion

    // region Age validation

    @Test
    fun `OnAgeChanged with blank calls onAgeChanged with NOT_A_NUMBER error`() = runTest {
        every { unwrapper.unwrap(any<StringResource>()) } returns "Not a number"

        eventChannel.send(AddUserStateHolderEvent.OnAgeChanged(""))

        coVerify { stateHolder.onAgeChanged("", "Not a number") }
    }

    @Test
    fun `OnAgeChanged with non-numeric string calls onAgeChanged with NOT_A_NUMBER error`() = runTest {
        every { unwrapper.unwrap(any<StringResource>()) } returns "Not a number"

        eventChannel.send(AddUserStateHolderEvent.OnAgeChanged("abc"))

        coVerify { stateHolder.onAgeChanged("abc", "Not a number") }
    }

    @Test
    fun `OnAgeChanged with age equal to AGE_MIN calls onAgeChanged with TOO_YOUNG error`() = runTest {
        every { unwrapper.unwrap(any<StringResource>()) } returns "Too young"

        eventChannel.send(AddUserStateHolderEvent.OnAgeChanged("18"))

        coVerify { stateHolder.onAgeChanged("18", "Too young") }
    }

    @Test
    fun `OnAgeChanged with age below AGE_MIN calls onAgeChanged with TOO_YOUNG error`() = runTest {
        every { unwrapper.unwrap(any<StringResource>()) } returns "Too young"

        eventChannel.send(AddUserStateHolderEvent.OnAgeChanged("17"))

        coVerify { stateHolder.onAgeChanged("17", "Too young") }
    }

    @Test
    fun `OnAgeChanged with age equal to AGE_MAX calls onAgeChanged with TOO_OLD error`() = runTest {
        every { unwrapper.unwrap(any<StringResource>()) } returns "Too old"

        eventChannel.send(AddUserStateHolderEvent.OnAgeChanged("80"))

        coVerify { stateHolder.onAgeChanged("80", "Too old") }
    }

    @Test
    fun `OnAgeChanged with age above AGE_MAX calls onAgeChanged with TOO_OLD error`() = runTest {
        every { unwrapper.unwrap(any<StringResource>()) } returns "Too old"

        eventChannel.send(AddUserStateHolderEvent.OnAgeChanged("81"))

        coVerify { stateHolder.onAgeChanged("81", "Too old") }
    }

    @Test
    fun `OnAgeChanged with valid age calls onAgeChanged with null error`() = runTest {
        every { unwrapper.unwrap(null as StringResource?) } returns null

        eventChannel.send(AddUserStateHolderEvent.OnAgeChanged("25"))

        coVerify { stateHolder.onAgeChanged("25", null) }
    }

    // endregion

    // region Job title validation

    @Test
    fun `OnJobTitleChanged with blank calls onJobChanged with REQUIRED error`() = runTest {
        every { unwrapper.unwrap(any<StringResource>()) } returns "Required"

        eventChannel.send(AddUserStateHolderEvent.OnJobTitleChanged(""))

        coVerify { stateHolder.onJobChanged("", "Required") }
    }

    @Test
    fun `OnJobTitleChanged exceeding max length calls onJobChanged with TOO_LONG error`() = runTest {
        every { unwrapper.unwrap(any<StringResource>()) } returns "Too long"
        val longTitle = "a".repeat(41)

        eventChannel.send(AddUserStateHolderEvent.OnJobTitleChanged(longTitle))

        coVerify { stateHolder.onJobChanged(longTitle, "Too long") }
    }

    @Test
    fun `OnJobTitleChanged with valid title calls onJobChanged with null error`() = runTest {
        every { unwrapper.unwrap(null as StringResource?) } returns null

        eventChannel.send(AddUserStateHolderEvent.OnJobTitleChanged("Software Engineer"))

        coVerify { stateHolder.onJobChanged("Software Engineer", null) }
    }

    @Test
    fun `OnJobTitleChanged with exactly max length calls onJobChanged with null error`() = runTest {
        every { unwrapper.unwrap(null as StringResource?) } returns null
        val maxTitle = "a".repeat(40)

        eventChannel.send(AddUserStateHolderEvent.OnJobTitleChanged(maxTitle))

        coVerify { stateHolder.onJobChanged(maxTitle, null) }
    }

    // endregion

    // region Gender validation

    @Test
    fun `OnGenderChanged with blank calls onGenderChanged with NOT_SELECTED error`() = runTest {
        every { unwrapper.unwrap(any<StringResource>()) } returns "Gender not selected"

        eventChannel.send(AddUserStateHolderEvent.OnGenderChanged(""))

        coVerify { stateHolder.onGenderChanged("", "Gender not selected") }
    }

    @Test
    fun `OnGenderChanged with valid gender calls onGenderChanged with null error`() = runTest {
        every { unwrapper.unwrap(null as StringResource?) } returns null

        eventChannel.send(AddUserStateHolderEvent.OnGenderChanged("Male"))

        coVerify { stateHolder.onGenderChanged("Male", null) }
    }

    // endregion

    // region Submit

    @Test
    fun `OnSubmitClicked with valid user emits NavigateToUserList and calls reset`() = runTest {
        val user = User(name = "John Doe", age = 25, jobTitle = "Developer", gender = "Male")
        every { stateHolder.currentUser() } returns user
        coEvery { addUserUseCase(user) } returns Result.success(Unit)

        val collectedEffects = mutableListOf<AddUserEffect>()
        val collectJob = launch { viewModel.effects.collect { collectedEffects.add(it) } }

        eventChannel.send(AddUserStateHolderEvent.OnSubmitClicked)
        advanceUntilIdle()

        assertThat(collectedEffects).contains(AddUserEffect.NavigateToUserList)
        verify { stateHolder.reset() }
        collectJob.cancel()
    }

    @Test
    fun `OnSubmitClicked with valid user sets submitting true before use case call`() = runTest {
        val user = User(name = "John Doe", age = 25, jobTitle = "Developer", gender = "Male")
        every { stateHolder.currentUser() } returns user
        coEvery { addUserUseCase(user) } returns Result.success(Unit)

        eventChannel.send(AddUserStateHolderEvent.OnSubmitClicked)
        advanceUntilIdle()

        verify { stateHolder.setSubmitting(true) }
    }

    @Test
    fun `OnSubmitClicked when use case fails calls setSubmitting false`() = runTest {
        val user = User(name = "John Doe", age = 25, jobTitle = "Developer", gender = "Male")
        every { stateHolder.currentUser() } returns user
        coEvery { addUserUseCase(user) } returns Result.failure(RuntimeException("error"))

        val collectedEffects = mutableListOf<AddUserEffect>()
        val collectJob = launch { viewModel.effects.collect { collectedEffects.add(it) } }

        eventChannel.send(AddUserStateHolderEvent.OnSubmitClicked)
        advanceUntilIdle()

        verify { stateHolder.setSubmitting(false) }
        assertThat(collectedEffects).isEmpty()
        collectJob.cancel()
    }

    @Test
    fun `OnSubmitClicked when currentUser is null does not invoke use case`() = runTest {
        every { stateHolder.currentUser() } returns null

        eventChannel.send(AddUserStateHolderEvent.OnSubmitClicked)
        advanceUntilIdle()

        coVerify(exactly = 0) { addUserUseCase(any()) }
    }

    // endregion
}

@OptIn(ExperimentalCoroutinesApi::class)
class UnconfinedDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}