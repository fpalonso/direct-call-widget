@file:JvmName("Device")

package com.blaxsoftware.directcallwidget

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.*
import android.os.Build
import android.provider.MediaStore

@SuppressLint("UnsupportedChromeOsCameraSystemFeature")
fun hasCamera(context: Context): Boolean = with(context.packageManager) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        hasSystemFeature(FEATURE_CAMERA_ANY)
    } else {
        hasSystemFeature(FEATURE_CAMERA) || hasSystemFeature(FEATURE_CAMERA_FRONT)
    }
}

fun hasCameraApp(context: Context): Boolean {
    return Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            .resolveActivity(context.packageManager) != null
}

fun canUseCamera(context: Context): Boolean {
    return hasCamera(context) && hasCameraApp(context)
}
