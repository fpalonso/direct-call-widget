package com.blaxsoftware.directcallwidget.file

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
}
