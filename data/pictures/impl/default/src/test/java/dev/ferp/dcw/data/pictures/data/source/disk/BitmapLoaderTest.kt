package dev.ferp.dcw.data.pictures.data.source.disk

import android.graphics.Bitmap
import com.bumptech.glide.load.engine.GlideException
import dev.ferp.dcw.data.pictures.data.source.disk.BitmapLoader.ContinuationRequestListener
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class BitmapLoaderTest {

    private lateinit var continuation: Continuation<Bitmap>
    private lateinit var requestListener: ContinuationRequestListener

    @Before
    fun setUp() {
        continuation = mockk(relaxed = true)
        requestListener = ContinuationRequestListener(continuation)
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