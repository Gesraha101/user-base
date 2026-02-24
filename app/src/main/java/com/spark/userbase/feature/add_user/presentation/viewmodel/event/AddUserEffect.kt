package com.spark.userbase.feature.add_user.presentation.viewmodel.event

sealed interface AddUserEffect {
    data object NavigateToUserList : AddUserEffect
}
