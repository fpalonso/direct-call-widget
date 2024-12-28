/*
 * Direct Call Widget - The widget that makes contacts accessible
 * Copyright (C) 2024 Fer P. A.
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

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.layout.ContentScale
import com.bumptech.glide.Glide
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition

@Composable
fun RemoteImage(
    model: String,
    @DrawableRes placeholderResId: Int,
    modifier: GlanceModifier = GlanceModifier,
    contentDescription: String? = null
) {
    val size = glanceLocalPixelSize()
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val target = RemoteImageTarget(
        onImageReady = { bitmap = it }
    )
    val options = RequestOptions()
        .override(size.width, size.height)
        .placeholder(placeholderResId)
    val context = LocalContext.current
    Glide
        .with(context)
        .asBitmap()
        .load(model)
        .centerInside()
        .apply(options)
        .into(target)
    bitmap?.let { image ->
        Image(
            modifier = modifier,
            contentScale = ContentScale.Crop,
            provider = ImageProvider(image),
            contentDescription = contentDescription
        )
    }
}

internal class RemoteImageTarget(
    private val onImageReady: (Bitmap) -> Unit,
) : BaseBitmapTarget {

    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
        onImageReady(resource)
    }
}

internal interface BaseBitmapTarget : Target<Bitmap> {

    override fun onStart() = Unit

    override fun onStop() = Unit

    override fun onDestroy() = Unit

    override fun onLoadStarted(placeholder: Drawable?) = Unit

    override fun onLoadFailed(errorDrawable: Drawable?) = Unit

    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) = Unit

    override fun onLoadCleared(placeholder: Drawable?) = Unit

    override fun getSize(cb: SizeReadyCallback) = Unit

    override fun removeCallback(cb: SizeReadyCallback) = Unit

    override fun setRequest(request: Request?) = Unit

    override fun getRequest(): Request? = null
}