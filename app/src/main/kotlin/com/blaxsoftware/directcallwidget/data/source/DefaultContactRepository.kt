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
import android.provider.ContactsContract.Contacts
import dev.ferp.dcw.core.androidutil.getStringOrNull
import dev.ferp.dcw.data.contacts.Contact
import dev.ferp.dcw.data.contacts.ContactRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultContactRepository @Inject constructor(
    private val contentResolver: ContentResolver
) : ContactRepository<Uri> {

    override suspend fun getContactById(id: Uri): Contact? {
        return withContext(Dispatchers.IO) {
            contentResolver.query(
                    id,
                    arrayOf(Contacts.DISPLAY_NAME, Contacts.PHOTO_URI, Contacts.LOOKUP_KEY),
                    null,
                    null,
                    null
            )?.use { cursor ->
                with(cursor) {
                    if (moveToFirst()) {
                        return@withContext Contact(
                            getStringOrNull(Contacts.DISPLAY_NAME) ?: "",
                            getStringOrNull(Contacts.PHOTO_URI),
                            getStringOrNull(Contacts.LOOKUP_KEY)
                        )
                    }
                }
            }
            return@withContext null
        }
    }
}
