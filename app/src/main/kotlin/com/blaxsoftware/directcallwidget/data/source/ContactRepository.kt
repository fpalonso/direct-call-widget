package com.blaxsoftware.directcallwidget.data.source

import android.content.ContentResolver
import android.net.Uri
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds
import android.provider.ContactsContract.Contacts
import androidx.annotation.WorkerThread
import com.blaxsoftware.directcallwidget.data.model.Contact
import com.blaxsoftware.directcallwidget.data.model.Phone
import getInt
import getString
import getUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContactRepository(
        private val contentResolver: ContentResolver
) : ContactDataSource {

    override suspend fun getContactByUri(contactUri: Uri): Contact? {
        return withContext(Dispatchers.IO) {
            contentResolver.query(
                    contactUri,
                    arrayOf(Contacts.DISPLAY_NAME, Contacts.PHOTO_URI, Contacts.LOOKUP_KEY),
                    null,
                    null,
                    null
            )?.use { cursor ->
                with(cursor) {
                    if (moveToFirst()) {
                        return@withContext Contact(
                                getString(Contacts.DISPLAY_NAME) ?: "",
                                getUri(Contacts.PHOTO_URI),
                                getPhoneListByLookupKey(getString(Contacts.LOOKUP_KEY))
                        )
                    }
                }
            }
            return@withContext null
        }
    }

    @WorkerThread
    private fun getPhoneListByLookupKey(lookupKey: String?): List<Phone> {
        val phoneList = mutableListOf<Phone>()
        contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                arrayOf(CommonDataKinds.Phone.NUMBER, CommonDataKinds.Phone.TYPE),
                "${CommonDataKinds.Phone.LOOKUP_KEY} = ? AND ${CommonDataKinds.Phone.MIMETYPE} = ?",
                arrayOf(lookupKey, CommonDataKinds.Phone.CONTENT_ITEM_TYPE),
                null
        )?.use { cursor ->
            with(cursor) {
                while (moveToNext()) {
                    Phone(
                            getString(CommonDataKinds.Phone.NUMBER) ?: "",
                            getInt(CommonDataKinds.Phone.TYPE)
                                    ?: CommonDataKinds.Phone.TYPE_HOME
                    ).also {
                        phoneList.add(it)
                    }
                }
            }
        }
        return phoneList
    }
}
