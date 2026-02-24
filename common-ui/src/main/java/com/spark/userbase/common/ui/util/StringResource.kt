package com.spark.userbase.common.ui.util

import androidx.annotation.StringRes

sealed class StringResource {
    data class Simple(@StringRes val resId: Int) : StringResource()
    class Format(@StringRes val resId: Int, vararg val args: Any) : StringResource()
}
