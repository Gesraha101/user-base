package com.spark.userbase.feature.add_user.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spark.userbase.R
import com.spark.userbase.common.ui.util.StringResource
import com.spark.userbase.common.ui.util.StringUnwrapper
import com.spark.userbase.feature.add_user.domain.usecase.AddUserUseCase
import com.spark.userbase.feature.add_user.presentation.viewmodel.event.AddUserEffect
import com.spark.userbase.feature.add_user.presentation.viewmodel.state.AddUserUiState
import com.spark.userbase.feature.add_user.presentation.viewmodel.state.AgeValidationError
import com.spark.userbase.feature.add_user.presentation.viewmodel.state.GenderValidationError
import com.spark.userbase.feature.add_user.presentation.viewmodel.state.JobTitleValidationError
import com.spark.userbase.feature.add_user.presentation.viewmodel.state.NameValidationError
import com.spark.userbase.feature.add_user.presentation.viewmodel.stateholder.AddUserStateHolder
import com.spark.userbase.feature.add_user.presentation.viewmodel.stateholder.event.AddUserStateHolderEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddUserViewModel @Inject constructor(
    private val addUserUseCase: AddUserUseCase,
    private val stateHolder: AddUserStateHolder,
    private val unwrapper: StringUnwrapper
) : ViewModel() {

    val state: StateFlow<AddUserUiState.Form> = stateHolder.state

    private val _effects = Channel<AddUserEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        viewModelScope.launch { observeStateHolderEvents() }
    }

    suspend fun observeStateHolderEvents() {
        stateHolder.events.collect {
            when (it) {
                is AddUserStateHolderEvent.OnAgeChanged -> {
                    val error = unwrapper.unwrap(validateAge(it.value)?.toMessage())
                    stateHolder.onAgeChanged(it.value, error)
                }
                is AddUserStateHolderEvent.OnGenderChanged -> {
                    val error = unwrapper.unwrap(validateGender(it.value)?.toMessage())
                    stateHolder.onGenderChanged(it.value, error)
                }
                is AddUserStateHolderEvent.OnJobTitleChanged -> {
                    val error = unwrapper.unwrap(validateJobTitle(it.value)?.toMessage())
                    stateHolder.onJobChanged(it.value, error)
                }
                is AddUserStateHolderEvent.OnNameChanged -> {
                    val error = unwrapper.unwrap(validateName(it.value)?.toMessage())
                    stateHolder.onNameChanged(it.value, error)
                }
                AddUserStateHolderEvent.OnSubmitClicked -> submit()
            }
        }
    }

    private fun validateName(name: String): NameValidationError? {
        if (name.trim().length <= NAME_MIN_LENGTH) return NameValidationError.TOO_SHORT
        if (name.any { !it.isLetter() && it != ' ' }) return NameValidationError.INVALID_CHARACTERS
        return null
    }

    private fun validateAge(age: String): AgeValidationError? {
        if (age.isBlank()) return AgeValidationError.NOT_A_NUMBER
        val ageInt = age.toIntOrNull() ?: return AgeValidationError.NOT_A_NUMBER
        if (ageInt <= AGE_MIN) return AgeValidationError.TOO_YOUNG
        if (ageInt >= AGE_MAX) return AgeValidationError.TOO_OLD
        return null
    }

    private fun validateJobTitle(jobTitle: String): JobTitleValidationError? {
        if (jobTitle.isBlank()) return JobTitleValidationError.REQUIRED
        if (jobTitle.length > JOB_TITLE_MAX_LENGTH) return JobTitleValidationError.TOO_LONG
        return null
    }

    private fun validateGender(gender: String): GenderValidationError? {
        if (gender.isBlank()) return GenderValidationError.NOT_SELECTED
        return null
    }

    private fun submit() {
        val user = stateHolder.currentUser() ?: return
        viewModelScope.launch {
            stateHolder.setSubmitting(true)
            val result = addUserUseCase(user)
            if (result.isSuccess) {
                _effects.send(AddUserEffect.NavigateToUserList)
                stateHolder.reset()
            } else {
                stateHolder.setSubmitting(false)
            }
        }
    }

    private fun NameValidationError.toMessage(): StringResource = when (this) {
        NameValidationError.TOO_SHORT -> StringResource.Format(R.string.error_name_too_short, NAME_MIN_LENGTH)
        NameValidationError.INVALID_CHARACTERS -> StringResource.Simple(R.string.error_name_invalid_chars)
    }

    private fun AgeValidationError.toMessage(): StringResource = when (this) {
        AgeValidationError.NOT_A_NUMBER -> StringResource.Simple(R.string.error_age_not_a_number)
        AgeValidationError.TOO_YOUNG -> StringResource.Format(R.string.error_age_too_young, AGE_MIN)
        AgeValidationError.TOO_OLD -> StringResource.Format(R.string.error_age_too_old, AGE_MAX)
    }

    private fun JobTitleValidationError.toMessage(): StringResource = when (this) {
        JobTitleValidationError.REQUIRED -> StringResource.Simple(R.string.error_job_title_required)
        JobTitleValidationError.TOO_LONG -> StringResource.Format(R.string.error_job_title_too_long, JOB_TITLE_MAX_LENGTH)
    }

    private fun GenderValidationError.toMessage(): StringResource = when (this) {
        GenderValidationError.NOT_SELECTED -> StringResource.Simple(R.string.error_gender_not_selected)
    }

    companion object {
        private const val NAME_MIN_LENGTH = 2
        private const val AGE_MIN = 18
        private const val AGE_MAX = 80
        private const val JOB_TITLE_MAX_LENGTH = 40
    }
}
