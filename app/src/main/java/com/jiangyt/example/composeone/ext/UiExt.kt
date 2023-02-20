package com.jiangyt.example.composeone.ext

import android.content.res.Resources
import android.util.TypedValue

val Int.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this * 1f,
        Resources.getSystem().displayMetrics
    )