package com.spark.userbase.nav

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import com.spark.userbase.AddUserRoute
import com.spark.userbase.UserListRoute
import com.spark.userbase.feature.add_user.presentation.ui.screen.AddUserScreen
import com.spark.userbase.feature.list_users.presentation.ui.screen.UserListScreen

fun getNavProvider(backStack: NavBackStack<NavKey>) = entryProvider {
    entry<AddUserRoute> {
        AddUserScreen(
            onNavigateToUserList = { backStack.add(UserListRoute) },
        )
    }
    entry<UserListRoute> {
        UserListScreen(onNavigateBackToAdd = { backStack.remove(UserListRoute) })
    }
}