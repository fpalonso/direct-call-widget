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

package com.blaxsoftware.directcallwidget.ui

import android.content.Context
import android.util.Size
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.glance.LocalContext
import androidx.glance.LocalSize

fun Context.xdpToPx(xdp: Int): Int {
    val scale: Float = resources.displayMetrics.xdpi / 160;
    return (xdp * scale).toInt()
}

fun Context.ydpToPx(ydp: Int): Int {
    val scale: Float = resources.displayMetrics.ydpi / 160;
    return (ydp * scale).toInt()
}

fun Context.pixelsX(dp: Dp) = xdpToPx(dp.value.toInt())

fun Context.pixelsY(dp: Dp) = ydpToPx(dp.value.toInt())

fun DpSize.asPixels(context: Context) = Size(
    context.pixelsX(width),
    context.pixelsY(height)
)

@Composable
fun glanceLocalPixelSize() = LocalSize.current.asPixels(LocalContext.current)