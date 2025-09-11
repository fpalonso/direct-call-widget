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
import android.graphics.Bitmap
import androidx.core.net.toUri
import dev.ferp.dcw.core.di.IoDispatcher
import dev.ferp.dcw.data.picture.di.PicturesDir
import dev.ferp.dcw.data.picture.source.PictureDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

internal class PictureDiskDataSource @Inject constructor(
    private val contentResolver: ContentResolver,
    @PicturesDir private val picturesDir: File,
    private val pictureLoader: PictureLoader,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : PictureDataSource {

    override suspend fun addPicture(pictureUri: String): Result<String> = withContext(ioDispatcher) {
        try {
            val destinationFile = createDestinationFile()
            destinationFile.outputStream().use { outputStream ->
                contentResolver.openInputStream(pictureUri.toUri())?.use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            Result.success(destinationFile.toUri().toString())
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    /** Creates an internal file to copy the source file to */
    private suspend fun createDestinationFile(): File = withContext(ioDispatcher) {
        picturesDir.mkdirs()
        val targetFile = File(picturesDir, UUID.randomUUID().toString())
        targetFile.createNewFile()
        targetFile
    }

    override suspend fun getPicture(
        pictureUri: String,
        widthPx: Int,
        heightPx: Int,
        placeholder: Int?
    ): Result<Bitmap> {
        return pictureLoader.loadPicture(
            pictureUri.toUri(),
            widthPx,
            heightPx,
            placeholder ?: 0
        )
    }

    override suspend fun deletePicture(pictureUri: String): Boolean {
        return pictureUri.toUri().path?.let {
            File(it).delete()
        } ?: false
    }
}