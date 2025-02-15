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
import com.bumptech.glide.load.engine.GlideException
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class BitmapLoaderTest {

    private lateinit var continuation: Continuation<Bitmap>
    private lateinit var requestListener: BitmapLoader.ContinuationRequestListener

    @Before
    fun setUp() {
        continuation = mockk(relaxed = true)
        requestListener = BitmapLoader.ContinuationRequestListener(continuation)
    }

    @Test
    fun `onResourceReady resumes continuation with bitmap`() {
        val expectedBitmap: Bitmap = mockk()
        requestListener.onResourceReady(expectedBitmap, null, null, null, true)
        verify { continuation.resume(expectedBitmap) }
    }

    @Test
    fun `onLoadFailed resumes continuation with exception`() {
        val expectedException = GlideException("Load failed")
        requestListener.onLoadFailed(expectedException, null, null, true)
        verify { continuation.resumeWithException(expectedException) }
    }
}