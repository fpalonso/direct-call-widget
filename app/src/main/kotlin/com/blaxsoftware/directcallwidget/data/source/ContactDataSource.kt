package com.blaxsoftware.directcallwidget.data.source

import android.net.Uri
import com.blaxsoftware.directcallwidget.data.model.Contact

interface ContactDataSource {

    suspend fun getContactByUri(contactUri: Uri): Contact?
}
