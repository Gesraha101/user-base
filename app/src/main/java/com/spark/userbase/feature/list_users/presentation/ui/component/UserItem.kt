package com.spark.userbase.feature.list_users.presentation.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.spark.userbase.feature.add_user.domain.model.User

class UserItem(
    val user: User,
    val genderColor: @Composable () -> Color,
)
