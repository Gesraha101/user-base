package com.spark.userbase.feature.list_users.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spark.userbase.feature.add_user.domain.usecase.GetUsersUseCase
import com.spark.userbase.feature.list_users.presentation.viewmodel.event.ListUsersEvent
import com.spark.userbase.feature.list_users.presentation.viewmodel.state.ListUsersUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListUsersViewModel @Inject constructor(
    private val getUsers: GetUsersUseCase,
) : ViewModel() {

    private val _events = Channel<ListUsersEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()
    private val _state = MutableStateFlow<ListUsersUiState>(ListUsersUiState.Loading)
    val state = _state.asStateFlow()

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            getUsers().collect { result ->
                val transformedResult = result.fold(
                    onSuccess = { users ->
                        ListUsersUiState.Success(
                            users,
                            onAddClicked = ::navigateToAdd
                        )
                    },
                    onFailure = {
                        ListUsersUiState.Error(
                            it.message.orEmpty(),
                            onRetry = ::loadUsers
                        )
                    },
                )

                _state.emit(transformedResult)
            }
        }
    }

    private fun navigateToAdd() {
        _events.trySend(ListUsersEvent.NavigateToAdd)
    }
}
