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
import android.net.Uri
import androidx.core.net.toUri
import dev.ferp.dcw.core.di.IoDispatcher
import dev.ferp.dcw.core.util.Files
import dev.ferp.dcw.core.util.Files.copyFile
import dev.ferp.dcw.data.pictures.di.PicturesDir
import dev.ferp.dcw.data.pictures.source.disk.PictureLoader
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

class DefaultWidgetPictureRepository @Inject constructor(
    private val contentResolver: ContentResolver,
    @PicturesDir private val picturesDir: File,
    private val pictureLoader: PictureLoader<Bitmap>,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : WidgetPictureRepository<Uri, Uri, Bitmap, Int> {

    override suspend fun addPicture(source: Uri): Uri {
        return withContext(ioDispatcher) {
            val targetFile = Files.createFile(
                dir = picturesDir,
                name = UUID.randomUUID().toString()
            )
            checkNotNull(targetFile)
            contentResolver.copyFile(source, targetFile)
            targetFile.toUri()
        }
    }

    override suspend fun getPicture(
        locator: Uri, widthPx: Int, heightPx: Int, placeholder: Int?
    ): Bitmap = pictureLoader.loadPicture(locator, widthPx, heightPx, placeholder ?: 0)

    override suspend fun deletePicture(locator: Uri): Boolean {
        return withContext(ioDispatcher) {
            locator.path?.let {
                File(it).delete()
            } ?: false
        }
    }
}