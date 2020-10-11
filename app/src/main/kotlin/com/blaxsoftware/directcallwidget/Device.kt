/*
 * Direct Call Widget - The widget that makes contacts accessible
 * Copyright (C) 2020 Fer P. A.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
