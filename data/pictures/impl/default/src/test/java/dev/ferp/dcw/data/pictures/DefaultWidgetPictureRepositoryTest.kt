package dev.ferp.dcw.data.pictures

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.BitmapFactory
import androidx.core.net.toFile
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.ferp.dcw.core.util.test.MainDispatcherRule
import dev.ferp.dcw.data.pictures.rules.ImageRule
import dev.ferp.dcw.data.pictures.source.disk.PictureLoader
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class DefaultWidgetPictureRepositoryTest {

    private val dispatcher: TestDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(dispatcher)

    @get:Rule
    val imageRule = ImageRule()

    private lateinit var context: Context
    private lateinit var contentResolver: ContentResolver
    private lateinit var picturesDir: File
    private lateinit var pictureLoader: PictureLoader<Bitmap>
    private lateinit var repo: DefaultWidgetPictureRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        contentResolver = context.contentResolver
        picturesDir = File(context.filesDir, "pics")
        pictureLoader = mockk()
        repo = DefaultWidgetPictureRepository(
            contentResolver, picturesDir, pictureLoader, dispatcher
        )
    }

    @Test
    fun `addPicture copies file from uri to pictures dir`() = runTest {
        // Given
        val sourceImage = imageRule.createSampleImageFile()

        // When
        val internalFileUri = repo.addPicture(sourceImage.fileUri)

        // Then
        val resultFile = internalFileUri.toFile()
        assertThat(resultFile.exists()).isTrue()
        assertThat(resultFile.parentFile).isEqualTo(picturesDir)
        val resultBitmap = BitmapFactory.decodeFile(internalFileUri.path)
        assertThat(resultBitmap.width).isEqualTo(sourceImage.width)
        assertThat(resultBitmap.height).isEqualTo(sourceImage.height)
        assertThat(resultBitmap.config).isEqualTo(sourceImage.config)
    }

    @Test
    fun `getPicture returns the right picture`() = runTest {
        // Given
        val expectedBitmap = Bitmap.createBitmap(100, 100, Config.ARGB_8888)
        coEvery {
            pictureLoader.loadPicture(any(), any(), any(), any())
        } returns expectedBitmap

        // When
        val internalBitmap = repo.getPicture(locator = mockk(), 200, 200)

        // Then
        assertThat(internalBitmap).isEqualTo(expectedBitmap)
    }

    @Test
    fun `deletePicture deletes the file from disk`() = runTest {
        // Given
        val sourceImage = imageRule.createSampleImageFile()
        val internalFileUri = repo.addPicture(sourceImage.fileUri)

        // When
        repo.deletePicture(internalFileUri)

        // Then
        assertThat(internalFileUri.toFile().exists()).isFalse()
    }
}