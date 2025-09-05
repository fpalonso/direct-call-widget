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

package dev.ferp.dcw.data.pictures

import android.content.ContentResolver
import android.graphics.Bitmap
import androidx.core.net.toUri
import dev.ferp.dcw.core.di.IoDispatcher
import dev.ferp.dcw.core.domain.data.picture.PictureRepository
import dev.ferp.dcw.data.pictures.di.PicturesDir
import dev.ferp.dcw.data.pictures.source.disk.PictureLoader
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

internal class DefaultPictureRepository @Inject constructor(
    private val contentResolver: ContentResolver,
    @PicturesDir private val picturesDir: File,
    private val pictureLoader: PictureLoader<Bitmap>,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): PictureRepository<Bitmap> {

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
        lateinit var targetFile: File
        do {
            targetFile = File(picturesDir, UUID.randomUUID().toString())
        } while (targetFile.exists())
        targetFile.createNewFile()
        targetFile
    }

    override suspend fun getPicture(
        pictureUri: String,
        widthPx: Int,
        heightPx: Int,
        placeholder: Int?
    ): Result<Bitmap> = withContext(ioDispatcher) {
        try {
            Result.success(
                pictureLoader.loadPicture(
                    pictureUri.toUri(),
                    widthPx,
                    heightPx,
                    placeholder ?: 0
                )
            )
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override suspend fun deletePicture(pictureUri: String): Boolean = withContext(ioDispatcher) {
        pictureUri.toUri().path?.let {
            File(it).delete()
        } ?: false
    }
}