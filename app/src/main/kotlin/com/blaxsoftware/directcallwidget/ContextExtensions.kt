package com.blaxsoftware.directcallwidget

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes

fun Context.inflate(
        @LayoutRes layoutResId: Int,
        parent: ViewGroup,
        attachToParent: Boolean = false
) {
    LayoutInflater.from(this).inflate(layoutResId, parent, attachToParent)
}
