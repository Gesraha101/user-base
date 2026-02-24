package com.spark.userbase.feature.list_users.presentation.viewmodel.state

import com.spark.userbase.feature.add_user.domain.model.User

sealed interface ListUsersUiState {
    data object Loading : ListUsersUiState
    data class Success(val users: List<User>, val onAddClicked: () -> Unit = {}) : ListUsersUiState
    data class Error(val value: String, val onRetry: () -> Unit = {}) : ListUsersUiState
}
