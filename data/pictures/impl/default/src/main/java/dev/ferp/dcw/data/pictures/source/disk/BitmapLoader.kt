/*
 * Direct Call Widget - The widget that makes contacts accessible
 * Copyright (C) 2025 Fer P. A.
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

package dev.ferp.dcw.data.pictures.source.disk

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.annotation.VisibleForTesting
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import dev.ferp.dcw.data.pictures.di.WidgetPictureGlideRequestBuilder
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class BitmapLoader @Inject constructor(
    @WidgetPictureGlideRequestBuilder private val glideRequestBuilder: RequestBuilder<Bitmap>,
    private val diskCacheStrategy: DiskCacheStrategy = DiskCacheStrategy.NONE
) : PictureLoader<Bitmap> {

    override suspend fun loadPicture(
        uri: Uri,
        widthPx: Int,
        heightPx: Int,
        placeholderId: Int
    ): Bitmap = suspendCoroutine { continuation ->
        glideRequestBuilder
            .centerCrop()
            .diskCacheStrategy(diskCacheStrategy)
            .addListener(ContinuationRequestListener(continuation))
            .apply(RequestOptions()
                .override(widthPx, heightPx)
                .placeholder(placeholderId)
            )
            .load(uri)
            .into(BaseBitmapTarget())
    }

    private class BaseBitmapTarget : Target<Bitmap> {
        override fun onStart() = Unit
        override fun onStop() = Unit
        override fun onDestroy() = Unit
        override fun onLoadStarted(placeholder: Drawable?) = Unit
        override fun onLoadFailed(errorDrawable: Drawable?) = Unit
        override fun onResourceReady(
            resource: Bitmap,
            transition: Transition<in Bitmap>?
        ) = Unit
        override fun onLoadCleared(placeholder: Drawable?) = Unit
        override fun getSize(cb: SizeReadyCallback) = Unit
        override fun removeCallback(cb: SizeReadyCallback) = Unit
        override fun setRequest(request: Request?) = Unit
        override fun getRequest(): Request? = null
    }

    @VisibleForTesting
    class ContinuationRequestListener(
        private val continuation: Continuation<Bitmap>
    ) : RequestListener<Bitmap> {

        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Bitmap>?,
            isFirstResource: Boolean
        ): Boolean {
            e?.let { continuation.resumeWithException(e) }
            return e != null
        }

        override fun onResourceReady(
            resource: Bitmap?,
            model: Any?,
            target: Target<Bitmap>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            resource?.let { continuation.resume(resource) }
            return resource != null
        }
    }
}
