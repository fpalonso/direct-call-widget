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

package dev.ferp.dcw.data.picture.source.local

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.ferp.dcw.data.picture.source.PictureDataSource
import dev.ferp.dcw.data.rules.MainDispatcherRule
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
import java.io.FileOutputStream
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class PictureDiskDataSourceTest {

    private val dispatcher: TestDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(dispatcher)

    private lateinit var context: Context
    private lateinit var contentResolver: ContentResolver
    private lateinit var picturesDir: File
    private lateinit var pictureLoader: PictureLoader
    private lateinit var dataSource: PictureDataSource

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        contentResolver = context.contentResolver
        picturesDir = File(context.filesDir, "pics")
        pictureLoader = mockk()
        dataSource = PictureDiskDataSource(
            contentResolver, picturesDir, pictureLoader, dispatcher
        )
    }

    @Test
    fun `addPicture copies file from uri to pictures dir`() = runTest {
        // Given
        val sourceImage = createSampleImageFile()

        // When
        val addPictureResult = dataSource.addPicture(sourceImage.fileUri.toString())

        // Then
        assertThat(addPictureResult.isSuccess).isTrue()

        val resultFile = addPictureResult.getOrNull()?.toUri()?.toFile()
        assertThat(resultFile?.exists()).isTrue()
        assertThat(resultFile?.parentFile).isEqualTo(picturesDir)

        val resultBitmap = BitmapFactory.decodeFile(addPictureResult.getOrNull()?.toUri()?.path)
        assertThat(resultBitmap.width).isEqualTo(sourceImage.width)
        assertThat(resultBitmap.height).isEqualTo(sourceImage.height)
        assertThat(resultBitmap.config).isEqualTo(sourceImage.config)
    }

    @Test
    fun `getPicture returns the right picture`() = runTest {
        // Given
        val expectedBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        coEvery {
            pictureLoader.loadPicture(any(), any(), any(), any())
        } returns Result.success(expectedBitmap)

        // When
        val getPictureResult = dataSource.getPicture("fakeUri", 200, 200, placeholder = 0)

        // Then
        assertThat(getPictureResult.isSuccess).isTrue()
        assertThat(getPictureResult.getOrNull()).isEqualTo(expectedBitmap)
    }

    @Test
    fun `deletePicture deletes the file from disk`() = runTest {
        // Given
        val sourceImage = createSampleImageFile()
        val addPictureResult = dataSource.addPicture(sourceImage.fileUri.toString())
        assertThat(addPictureResult.isSuccess).isTrue()

        // When
        dataSource.deletePicture(addPictureResult.getOrNull()!!)

        // Then
        assertThat(addPictureResult.getOrNull()?.toUri()?.toFile()?.exists()).isFalse()
    }

    private fun createSampleImageFile(
        prefix: String = UUID.randomUUID().toString(),
        suffix: String = ".png",
        width: Int = 100,
        height: Int = 100,
        config: Bitmap.Config = Bitmap.Config.ARGB_8888,
        compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
        quality: Int = 100
    ): Image {
        val tempFile = File.createTempFile(prefix, suffix)
        FileOutputStream(tempFile).use { outStream ->
            Bitmap
                .createBitmap(width, height, config)
                .compress(compressFormat, quality, outStream)
        }
        return Image(
            file = tempFile,
            fileUri = tempFile.toUri(),
            filePath = tempFile.path,
            width = width,
            height = height,
            config = config,
            compressFormat = compressFormat
        )
    }

    /** Contains information about the image created. */
    private data class Image(
        val file: File,
        val fileUri: Uri,
        val filePath: String,
        val width: Int,
        val height: Int,
        val config: Bitmap.Config,
        val compressFormat: Bitmap.CompressFormat,
    )
}