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
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds
import android.provider.ContactsContract.Contacts
import androidx.annotation.WorkerThread
import com.blaxsoftware.directcallwidget.data.Contact
import com.blaxsoftware.directcallwidget.data.Phone
import com.blaxsoftware.directcallwidget.getInt
import com.blaxsoftware.directcallwidget.getString
import com.blaxsoftware.directcallwidget.getUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultContactRepository @Inject constructor(
    private val contentResolver: ContentResolver
) : ContactRepository {

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
