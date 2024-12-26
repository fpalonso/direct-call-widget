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

package com.blaxsoftware.directcallwidget.data.pictures

import android.content.ContentResolver
import android.net.Uri
import androidx.core.net.toUri
import com.blaxsoftware.directcallwidget.di.PicturesDir
import com.blaxsoftware.directcallwidget.file.Files
import com.blaxsoftware.directcallwidget.file.Files.copyFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

class DefaultWidgetPictureRepository @Inject constructor(
    private val contentResolver: ContentResolver,
    @PicturesDir private val picturesDir: File
) : WidgetPictureRepository {

    override suspend fun copyFromUri(uri: Uri): Uri = withContext(Dispatchers.IO) {
        val targetFile = Files.createFile(picturesDir, name = UUID.randomUUID().toString())
        checkNotNull(targetFile)
        contentResolver.copyFile(uri, targetFile)
        targetFile.toUri()
    }

    override suspend fun delete(uri: Uri) = withContext(Dispatchers.IO) {
        uri.path?.let { File(it).delete() }
    }
}