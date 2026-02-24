package com.spark.userbase.feature.add_user.presentation.viewmodel.state

sealed interface AddUserUiState {
    data class Form(
        val genderOptions: List<String>,
        val name: InputFieldState,
        val age: InputFieldState,
        val jobTitle: InputFieldState,
        val gender: InputFieldState,
        val isFormValid: Boolean = false,
        val isSubmitting: Boolean = false,
        val onSubmit: () -> Unit
    ) : AddUserUiState
}

enum class NameValidationError { TOO_SHORT, INVALID_CHARACTERS }

enum class AgeValidationError { NOT_A_NUMBER, TOO_YOUNG, TOO_OLD }

enum class JobTitleValidationError { REQUIRED, TOO_LONG }

enum class GenderValidationError { NOT_SELECTED }
