package com.blaxsoftware.directcallwidget.data.source

import android.content.ContentResolver
import android.net.Uri
import androidx.core.os.bundleOf
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
        GlobalScope.launch(Dispatchers.IO) {
            contentResolver.delete(uri, null, null)
        }
    }
}
