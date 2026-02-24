package com.spark.userbase.feature.list_users.presentation.viewmodel.event

sealed interface ListUsersEvent {
    data object NavigateToAdd : ListUsersEvent
}