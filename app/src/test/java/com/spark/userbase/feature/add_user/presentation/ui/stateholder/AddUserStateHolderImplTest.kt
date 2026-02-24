package com.spark.userbase.feature.add_user.presentation.ui.stateholder

import com.google.common.truth.Truth.assertThat
import com.spark.userbase.common.ui.util.StringUnwrapper
import com.spark.userbase.feature.add_user.domain.model.User
import com.spark.userbase.feature.add_user.presentation.viewmodel.stateholder.event.AddUserStateHolderEvent
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class AddUserStateHolderImplTest {

    @MockK lateinit var unwrapper: StringUnwrapper

    private lateinit var stateHolder: AddUserStateHolderImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { unwrapper.unwrap(any<Int>()) } answers { "option_${firstArg<Int>()}" }
        stateHolder = AddUserStateHolderImpl(unwrapper)
    }

    // region Initial state

    @Test
    fun `initial state has two gender options from unwrapper`() {
        assertThat(stateHolder.state.value.genderOptions).hasSize(2)
    }

    @Test
    fun `initial state has empty field values`() {
        val form = stateHolder.state.value
        assertThat(form.name.value).isEmpty()
        assertThat(form.age.value).isEmpty()
        assertThat(form.jobTitle.value).isEmpty()
        assertThat(form.gender.value).isEmpty()
    }

    @Test
    fun `initial state has all fields untouched`() {
        val form = stateHolder.state.value
        assertThat(form.name.isTouched).isFalse()
        assertThat(form.age.isTouched).isFalse()
        assertThat(form.jobTitle.isTouched).isFalse()
        assertThat(form.gender.isTouched).isFalse()
    }

    @Test
    fun `initial state has isFormValid false`() {
        assertThat(stateHolder.state.value.isFormValid).isFalse()
    }

    @Test
    fun `initial state has isSubmitting false`() {
        assertThat(stateHolder.state.value.isSubmitting).isFalse()
    }

    // endregion

    // region Field update methods

    @Test
    fun `onNameChanged updates name value and marks field as touched`() {
        stateHolder.onNameChanged("John", null)

        val name = stateHolder.state.value.name
        assertThat(name.value).isEqualTo("John")
        assertThat(name.isTouched).isTrue()
        assertThat(name.error).isNull()
    }

    @Test
    fun `onNameChanged with error stores error on name field`() {
        stateHolder.onNameChanged("A", "Name too short")

        assertThat(stateHolder.state.value.name.error).isEqualTo("Name too short")
    }

    @Test
    fun `onAgeChanged updates age value and marks field as touched`() {
        stateHolder.onAgeChanged("25", null)

        val age = stateHolder.state.value.age
        assertThat(age.value).isEqualTo("25")
        assertThat(age.isTouched).isTrue()
        assertThat(age.error).isNull()
    }

    @Test
    fun `onAgeChanged with error stores error on age field`() {
        stateHolder.onAgeChanged("17", "Too young")

        assertThat(stateHolder.state.value.age.error).isEqualTo("Too young")
    }

    @Test
    fun `onJobChanged updates jobTitle value and marks field as touched`() {
        stateHolder.onJobChanged("Developer", null)

        val jobTitle = stateHolder.state.value.jobTitle
        assertThat(jobTitle.value).isEqualTo("Developer")
        assertThat(jobTitle.isTouched).isTrue()
        assertThat(jobTitle.error).isNull()
    }

    @Test
    fun `onJobChanged with error stores error on jobTitle field`() {
        stateHolder.onJobChanged("", "Required")

        assertThat(stateHolder.state.value.jobTitle.error).isEqualTo("Required")
    }

    @Test
    fun `onGenderChanged updates gender value and marks field as touched`() {
        stateHolder.onGenderChanged("Male", null)

        val gender = stateHolder.state.value.gender
        assertThat(gender.value).isEqualTo("Male")
        assertThat(gender.isTouched).isTrue()
        assertThat(gender.error).isNull()
    }

    @Test
    fun `onGenderChanged with error stores error on gender field`() {
        stateHolder.onGenderChanged("", "Not selected")

        assertThat(stateHolder.state.value.gender.error).isEqualTo("Not selected")
    }

    // endregion

    // region isFormValid

    @Test
    fun `isFormValid is true when all fields touched and all errors null`() {
        fillFormValid()

        assertThat(stateHolder.state.value.isFormValid).isTrue()
    }

    @Test
    fun `isFormValid is false when one field has error`() {
        fillFormValid()
        stateHolder.onNameChanged("A", "Too short")

        assertThat(stateHolder.state.value.isFormValid).isFalse()
    }

    @Test
    fun `isFormValid is false when a field is not touched`() {
        stateHolder.onNameChanged("John Doe", null)
        stateHolder.onAgeChanged("25", null)
        stateHolder.onJobChanged("Developer", null)

        assertThat(stateHolder.state.value.isFormValid).isFalse()
    }

    // endregion

    // region setSubmitting

    @Test
    fun `setSubmitting true updates isSubmitting to true`() {
        stateHolder.setSubmitting(true)

        assertThat(stateHolder.state.value.isSubmitting).isTrue()
    }

    @Test
    fun `setSubmitting false updates isSubmitting to false`() {
        stateHolder.setSubmitting(true)
        stateHolder.setSubmitting(false)

        assertThat(stateHolder.state.value.isSubmitting).isFalse()
    }

    // endregion

    // region currentUser

    @Test
    fun `currentUser returns null when form is not valid`() {
        assertThat(stateHolder.currentUser()).isNull()
    }

    @Test
    fun `currentUser returns User with correct values when form is valid`() {
        fillFormValid()

        val user = stateHolder.currentUser()

        assertThat(user).isEqualTo(
            User(name = "John Doe", age = 25, jobTitle = "Developer", gender = "Male")
        )
    }

    @Test
    fun `currentUser trims whitespace from name and jobTitle`() {
        stateHolder.onNameChanged("  John Doe  ", null)
        stateHolder.onAgeChanged("25", null)
        stateHolder.onJobChanged("  Developer  ", null)
        stateHolder.onGenderChanged("Male", null)

        val user = stateHolder.currentUser()

        assertThat(user?.name).isEqualTo("John Doe")
        assertThat(user?.jobTitle).isEqualTo("Developer")
    }

    // endregion

    // region reset

    @Test
    fun `reset restores initial empty state`() {
        fillFormValid()

        stateHolder.reset()

        val form = stateHolder.state.value
        assertThat(form.name.value).isEmpty()
        assertThat(form.age.value).isEmpty()
        assertThat(form.jobTitle.value).isEmpty()
        assertThat(form.gender.value).isEmpty()
        assertThat(form.isFormValid).isFalse()
    }

    @Test
    fun `reset clears touched flags`() {
        fillFormValid()

        stateHolder.reset()

        val form = stateHolder.state.value
        assertThat(form.name.isTouched).isFalse()
        assertThat(form.age.isTouched).isFalse()
        assertThat(form.jobTitle.isTouched).isFalse()
        assertThat(form.gender.isTouched).isFalse()
    }

    // endregion

    // region Lambda → event emission

    @Test
    fun `calling name onChanged emits OnNameChanged event`() = runTest {
        val events = mutableListOf<AddUserStateHolderEvent>()
        val collectJob = launch { stateHolder.events.collect { events.add(it) } }

        stateHolder.state.value.name.onChanged("John")
        advanceUntilIdle()

        assertThat(events).contains(AddUserStateHolderEvent.OnNameChanged("John"))
        collectJob.cancel()
    }

    @Test
    fun `calling name onFocusLost emits OnNameChanged event`() = runTest {
        val events = mutableListOf<AddUserStateHolderEvent>()
        val collectJob = launch { stateHolder.events.collect { events.add(it) } }

        stateHolder.state.value.name.onFocusLost("John")
        advanceUntilIdle()

        assertThat(events).contains(AddUserStateHolderEvent.OnNameChanged("John"))
        collectJob.cancel()
    }

    @Test
    fun `calling age onChanged filters non-digit characters before emitting`() = runTest {
        val events = mutableListOf<AddUserStateHolderEvent>()
        val collectJob = launch { stateHolder.events.collect { events.add(it) } }

        stateHolder.state.value.age.onChanged("2a5b")
        advanceUntilIdle()

        assertThat(events).contains(AddUserStateHolderEvent.OnAgeChanged("25"))
        collectJob.cancel()
    }

    @Test
    fun `calling jobTitle onChanged emits OnJobTitleChanged event`() = runTest {
        val events = mutableListOf<AddUserStateHolderEvent>()
        val collectJob = launch { stateHolder.events.collect { events.add(it) } }

        stateHolder.state.value.jobTitle.onChanged("Developer")
        advanceUntilIdle()

        assertThat(events).contains(AddUserStateHolderEvent.OnJobTitleChanged("Developer"))
        collectJob.cancel()
    }

    @Test
    fun `calling gender onChanged emits OnGenderChanged event`() = runTest {
        val events = mutableListOf<AddUserStateHolderEvent>()
        val collectJob = launch { stateHolder.events.collect { events.add(it) } }

        stateHolder.state.value.gender.onChanged("Male")
        advanceUntilIdle()

        assertThat(events).contains(AddUserStateHolderEvent.OnGenderChanged("Male"))
        collectJob.cancel()
    }

    @Test
    fun `calling onSubmit emits OnSubmitClicked event`() = runTest {
        val events = mutableListOf<AddUserStateHolderEvent>()
        val collectJob = launch { stateHolder.events.collect { events.add(it) } }

        stateHolder.state.value.onSubmit()
        advanceUntilIdle()

        assertThat(events).contains(AddUserStateHolderEvent.OnSubmitClicked)
        collectJob.cancel()
    }

    // endregion

    private fun fillFormValid() {
        stateHolder.onNameChanged("John Doe", null)
        stateHolder.onAgeChanged("25", null)
        stateHolder.onJobChanged("Developer", null)
        stateHolder.onGenderChanged("Male", null)
    }
}
