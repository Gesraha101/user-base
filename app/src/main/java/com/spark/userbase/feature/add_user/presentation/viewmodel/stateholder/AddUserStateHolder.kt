package com.spark.userbase.feature.add_user.presentation.viewmodel.stateholder

import com.spark.userbase.feature.add_user.domain.model.User
import com.spark.userbase.feature.add_user.presentation.viewmodel.state.AddUserUiState
import com.spark.userbase.feature.add_user.presentation.viewmodel.stateholder.event.AddUserStateHolderEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AddUserStateHolder {
    val events: Flow<AddUserStateHolderEvent>
    val state: StateFlow<AddUserUiState.Form>
    fun onAgeChanged(value: String, error: String?)
    fun onNameChanged(value: String, error: String?)
    fun onJobChanged(value: String, error: String?)
    fun onGenderChanged(value: String, error: String?)
    fun setSubmitting(isSubmitting: Boolean)
    fun currentUser(): User?
    fun reset()
}
