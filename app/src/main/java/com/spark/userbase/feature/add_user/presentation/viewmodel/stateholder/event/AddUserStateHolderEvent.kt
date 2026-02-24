package com.spark.userbase.feature.add_user.presentation.viewmodel.stateholder.event

sealed interface AddUserStateHolderEvent {
    data class OnNameChanged(val value: String) : AddUserStateHolderEvent
    data class OnAgeChanged(val value: String) : AddUserStateHolderEvent
    data class OnJobTitleChanged(val value: String) : AddUserStateHolderEvent
    data class OnGenderChanged(val value: String) : AddUserStateHolderEvent
    data object OnSubmitClicked : AddUserStateHolderEvent
}