package com.spark.userbase.common.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class ExtraColors(
    val maleLabel: Color,
    val femaleLabel: Color,
)

internal val LocalExtraColors = staticCompositionLocalOf {
    ExtraColors(
        maleLabel = MaleLabelColor,
        femaleLabel = FemaleLabelColor,
    )
}

val MaterialTheme.extraColors: ExtraColors
    @Composable
    @ReadOnlyComposable
    get() = LocalExtraColors.current
