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

package dev.ferp.dcw.data.pictures.rules

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Bitmap.Config
import android.net.Uri
import androidx.core.net.toUri
import org.junit.rules.TestWatcher
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

/** Rule for creating sample image files in Robolectric */
class ImageRule : TestWatcher() {

    fun createSampleImageFile(
        prefix: String = UUID.randomUUID().toString(),
        suffix: String = ".png",
        width: Int = 100,
        height: Int = 100,
        config: Config = Config.ARGB_8888,
        compressFormat: CompressFormat = CompressFormat.PNG,
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
    data class Image(
        val file: File,
        val fileUri: Uri,
        val filePath: String,
        val width: Int,
        val height: Int,
        val config: Config,
        val compressFormat: CompressFormat,
    )
}