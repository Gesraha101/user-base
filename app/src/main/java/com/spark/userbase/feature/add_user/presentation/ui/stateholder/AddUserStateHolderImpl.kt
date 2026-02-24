package com.spark.userbase.feature.add_user.presentation.ui.stateholder

import com.spark.userbase.R
import com.spark.userbase.common.ui.util.StringUnwrapper
import com.spark.userbase.feature.add_user.domain.model.User
import com.spark.userbase.feature.add_user.presentation.viewmodel.state.AddUserUiState
import com.spark.userbase.feature.add_user.presentation.viewmodel.state.InputFieldState
import com.spark.userbase.feature.add_user.presentation.viewmodel.stateholder.AddUserStateHolder
import com.spark.userbase.feature.add_user.presentation.viewmodel.stateholder.event.AddUserStateHolderEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class AddUserStateHolderImpl @Inject constructor(private val unwrapper: StringUnwrapper) :
    AddUserStateHolder {

    private val _initialState = AddUserUiState.Form(
        genderOptions = listOf(
            R.string.gender_male,
            R.string.gender_female
        ).map { unwrapper.unwrap(it).orEmpty() },
        name = InputFieldState(
            onChanged = ::onNameValueChanged,
            onFocusLost = ::onNameValueChanged
        ),
        age = InputFieldState(onChanged = ::onAgeValueChanged, onFocusLost = ::onAgeValueChanged),
        jobTitle = InputFieldState(
            onChanged = ::onJobValueChanged,
            onFocusLost = ::onJobValueChanged
        ),
        gender = InputFieldState(
            onChanged = ::onGenderValueChanged,
            onFocusLost = ::onGenderValueChanged
        ),
        onSubmit = { _events.trySend(AddUserStateHolderEvent.OnSubmitClicked) }
    )

    private val _events = Channel<AddUserStateHolderEvent>(Channel.BUFFERED)
    override val events = _events.receiveAsFlow()
    private val _state = MutableStateFlow(_initialState)
    override val state: StateFlow<AddUserUiState.Form> = _state.asStateFlow()

    private fun InputFieldState.onTouched() = copy(isTouched = true)

    private fun onAgeValueChanged(value: String) {
        _events.trySend(AddUserStateHolderEvent.OnAgeChanged(value.filter { it.isDigit() }))
    }

    private fun onNameValueChanged(value: String) {
        _events.trySend(AddUserStateHolderEvent.OnNameChanged(value))
    }

    private fun onJobValueChanged(value: String) {
        _events.trySend(AddUserStateHolderEvent.OnJobTitleChanged(value))
    }

    private fun onGenderValueChanged(value: String) {
        _events.trySend(AddUserStateHolderEvent.OnGenderChanged(value))
    }

    override fun onAgeChanged(value: String, error: String?) {
        _state.update {
            with(
                it.copy(
                    age = it.age.copy(value = value, error = error).onTouched(),
                )
            ) {
                copy(isFormValid = isAllValid)
            }
        }
    }

    override fun onNameChanged(value: String, error: String?) {
        _state.update {
            with(
                it.copy(
                    name = it.name.copy(value = value, error = error).onTouched(),
                )
            ) {
                copy(isFormValid = isAllValid)
            }
        }
    }

    override fun onJobChanged(value: String, error: String?) {
        _state.update {
            with(
                it.copy(
                    jobTitle = it.jobTitle.copy(value = value, error = error).onTouched()
                )
            ) {
                copy(isFormValid = isAllValid)
            }
        }
    }

    override fun onGenderChanged(value: String, error: String?) {
        _state.update {
            with(
                it.copy(
                    gender = it.gender.copy(value = value, error = error).onTouched(),
                )
            ) { copy(isFormValid = isAllValid) }
        }
    }

    private val AddUserUiState.Form.isAllValid
        get() =
            name.error == null && jobTitle.error == null && age.error == null && gender.error == null &&
                    name.isTouched && jobTitle.isTouched && age.isTouched && gender.isTouched

    override fun setSubmitting(isSubmitting: Boolean) {
        _state.value = _state.value.copy(isSubmitting = isSubmitting)
    }

    override fun currentUser(): User? {
        if (!_state.value.isFormValid) return null
        val form = _state.value
        return User(
            name = form.name.value.trim(),
            age = form.age.value.toIntOrNull() ?: return null,
            jobTitle = form.jobTitle.value.trim(),
            gender = form.gender.value,
        )
    }

    override fun reset() {
        _state.value = _initialState
    }
}
