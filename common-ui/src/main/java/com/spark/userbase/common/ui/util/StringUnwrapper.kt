package com.spark.userbase.common.ui.util

import android.content.Context
import androidx.annotation.StringRes

class StringUnwrapper(private val context: Context) {
    fun unwrap(@StringRes res: Int?): String? = res?.let { context.getString(it) }

    fun unwrap(res: StringResource?): String? = when (res) {
        null -> null
        is StringResource.Simple -> context.getString(res.resId)
        is StringResource.Format -> context.getString(res.resId, *res.args)
    }
}