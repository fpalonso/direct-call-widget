/*
 * Direct Call Widget - The widget that makes contacts accessible
 * Copyright (C) 2020 Fer P. A.
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

package com.blaxsoftware.directcallwidget.data.source

import android.content.ContentResolver
import android.net.Uri
import com.blaxsoftware.directcallwidget.file.Files
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.*

class WidgetPicRepository(
        private val contentResolver: ContentResolver,
        private val picsDir: File
) : WidgetPicDataSource {

    @Throws(IOException::class)
    override suspend fun insertFromUri(uri: Uri): File? = withContext(Dispatchers.IO) {
        val targetFile = try {
            Files.createFile(picsDir, fileName = "${UUID.randomUUID()}")
        } catch (e: Throwable) {
            throw IOException("Could not insert picture from $uri: ${e.message}")
        }
        return@withContext targetFile?.also { file ->
            copyFile(uri, file)
        }
    }

    @Throws(IOException::class)
    private suspend fun copyFile(source: Uri, target: File) = withContext(Dispatchers.IO) {
        target.outputStream().use { out ->
            contentResolver.openInputStream(source)?.let {
                it.use { `in` ->
                    Files.copyFile(`in`, out)
                }
            }
        }
    }

    @Throws(IOException::class)
    override fun delete(uri: Uri) {
        uri.path?.let { picPath ->
            GlobalScope.launch(Dispatchers.IO) {
                File(picPath).delete()
            }
        }
    }
}
