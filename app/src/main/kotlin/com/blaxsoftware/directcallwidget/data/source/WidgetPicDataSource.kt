package com.blaxsoftware.directcallwidget.data.source

import android.net.Uri
import java.io.File

interface WidgetPicDataSource {

    suspend fun insertFromUri(uri: Uri): File?

    fun delete(uri: Uri)
}
