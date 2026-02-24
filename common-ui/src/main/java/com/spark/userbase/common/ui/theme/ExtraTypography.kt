package com.spark.userbase.common.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

data class ExtraTypography(
    val userItemName: TextStyle,
    val userItemJobTitle: TextStyle,
    val userItemAge: TextStyle,
)

internal val LocalExtraTypography = staticCompositionLocalOf {
    ExtraTypography(
        userItemName = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp),
        userItemJobTitle = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp),
        userItemAge = TextStyle(fontSize = 48.sp),
    )
}

val MaterialTheme.extraTypography: ExtraTypography
    @Composable
    @ReadOnlyComposable
    get() = LocalExtraTypography.current
