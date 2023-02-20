package com.jiangyt.app.carremote.utils

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue

val Int.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this * 1f,
        Resources.getSystem().displayMetrics
    )

val Int.sp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this * 1f,
        Resources.getSystem().displayMetrics
    )

fun Context.screenWidth(): Int {
    return this.resources.displayMetrics.widthPixels
}

fun Context.screenHeight(): Int {
    return this.resources.displayMetrics.heightPixels
}

