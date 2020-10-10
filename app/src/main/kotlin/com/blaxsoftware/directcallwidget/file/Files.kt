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

package com.blaxsoftware.directcallwidget.file

import android.content.Context
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object Files {

    @Throws(IOException::class, IllegalArgumentException::class)
    suspend fun createFile(dir: File, fileName: String): File? = withContext(Dispatchers.IO) {
        if (!dir.exists() && !dir.mkdirs()) {
            throw IOException("${dir.absolutePath} does not exist and could not be created")
        }
        if (!dir.isDirectory) {
            throw IllegalArgumentException("${dir.absolutePath} is not a valid directory")
        }
        File(dir, fileName).also { file ->
            return@withContext if (file.createNewFile()) file else null
        }
    }

    @Throws(IOException::class)
    suspend fun copyFile(`in`: InputStream, out: OutputStream) = withContext(Dispatchers.IO) {
        val buffer = ByteArray(1024)
        var len: Int
        do {
            len = `in`.read(buffer)
            if (len > 0) out.write(buffer, 0, len)
        } while (len > 0)
    }

    @Throws(IOException::class)
    suspend fun createCameraOutputFile(context: Context): File? = withContext(Dispatchers.IO) {
        val fileDir: File? = context.getExternalFilesDir("${Environment.DIRECTORY_PICTURES}/pics")
        return@withContext try {
            fileDir?.let {
                File.createTempFile("cam-", ".jpg", fileDir)
            }
        } catch (e: IOException) {
            null
        }
    }
}
