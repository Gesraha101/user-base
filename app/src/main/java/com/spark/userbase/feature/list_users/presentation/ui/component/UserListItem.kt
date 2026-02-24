package com.spark.userbase.feature.list_users.presentation.ui.component

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.spark.userbase.common.ui.theme.extraTypography
import com.spark.userbase.common.ui.theme.spacing

@Composable
fun UserListItem(item: UserItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.user.name,
                style = MaterialTheme.extraTypography.userItemName,
            )
            Text(
                text = item.user.jobTitle,
                style = MaterialTheme.extraTypography.userItemJobTitle,
            )
            Text(
                text = item.user.gender,
                color = item.genderColor(),
            )
        }
        var targetAge by remember { mutableIntStateOf(0) }
        val animatedAge by animateIntAsState(
            targetValue = targetAge,
            animationSpec = tween(1000),
        )
        LaunchedEffect(Unit) {
            targetAge = item.user.age
        }
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = animatedAge.toString(),
                style = MaterialTheme.extraTypography.userItemAge,
            )
        }
    }
}
