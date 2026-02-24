package com.spark.userbase.feature.add_user.presentation.viewmodel.state

data class InputFieldState(
    val value: String = "",
    val isTouched: Boolean = false,
    val error: String? = null,
    val onChanged: (value: String) -> Unit,
    val onFocusLost: (value: String) -> Unit,
) {
    val displayError: String? get() = if (isTouched) error else null
}
