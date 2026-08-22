/*
 * Direct Call Widget - The widget that makes contacts accessible
 * Copyright (C) 2025 Fer P. A.
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

package dev.ferp.dcw.data.devicecontact

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import androidx.core.net.toUri
import dev.ferp.dcw.core.di.IoDispatcher
import dev.ferp.dcw.core.domain.data.PhoneType
import dev.ferp.dcw.core.domain.data.devicecontact.ContactRepository
import dev.ferp.dcw.core.domain.data.devicecontact.Contact
import dev.ferp.dcw.core.domain.data.devicecontact.Phone
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class DefaultContactRepository @Inject constructor(
    private val contentResolver: ContentResolver,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ContactRepository {

    override suspend fun getContactByUri(contactUri: String): Result<Contact> {
        return getLookupKey(contactUri.toUri()).fold(
            onSuccess = { lookupKey -> getContact(lookupKey) },
            onFailure = { Result.failure(it) }
        )
    }

    override suspend fun getFavoriteContacts(): Result<List<Contact>> {
        return getFavoriteLookupKeys().fold(
            onSuccess = { lookupKeys -> getContacts(lookupKeys) },
            onFailure = { Result.failure(it) }
        )
    }

    private suspend fun getLookupKey(contactUri: Uri): Result<String> {
        return queryContentResolver(
            uri = contactUri,
            projection = arrayOf(ContactsContract.Contacts.LOOKUP_KEY)
        ) { cursor ->
            if (!cursor.moveToFirst()) {
                return@queryContentResolver Result.failure(IOException("Contact not found for URI"))
            }
            val lookupKey = cursor.getString(
                cursor.getColumnIndexOrThrow(ContactsContract.Contacts.LOOKUP_KEY)
            ) ?: return@queryContentResolver Result.failure(IOException("LookupKey is null"))
            Result.success(lookupKey)
        }
    }

    private suspend fun getContact(lookupKey: String): Result<Contact> {
        return getContacts(listOf(lookupKey)).mapCatching { contacts ->
            contacts.firstOrNull()
                ?: throw IOException("No contact with phone number found for lookup key")
        }
    }

    private suspend fun getFavoriteLookupKeys(): Result<List<String>> {
        return queryContentResolver(
            uri = ContactsContract.Contacts.CONTENT_URI,
            projection = arrayOf(ContactsContract.Contacts.LOOKUP_KEY),
            selection = "${ContactsContract.Contacts.STARRED} = ?",
            selectionArgs = arrayOf("1")
        ) { cursor ->
            val keys: MutableList<String> = mutableListOf()
            while (cursor.moveToNext()) {
                val lookupKey = cursor.getString(
                    cursor.getColumnIndexOrThrow(ContactsContract.Contacts.LOOKUP_KEY)
                ) ?: continue
                keys.add(lookupKey)
            }
            Result.success(keys)
        }
    }

    private suspend fun getContacts(lookupKeys: List<String>): Result<List<Contact>> {
        if (lookupKeys.isEmpty()) return Result.success(emptyList())

        val lookupKeyColumn = ContactsContract.Contacts.LOOKUP_KEY
        val displayNameColumn = ContactsContract.Contacts.DISPLAY_NAME
        val photoUriColumn = ContactsContract.Contacts.PHOTO_URI
        val phoneNumberColumn = ContactsContract.CommonDataKinds.Phone.NUMBER
        val phoneTypeColumn = ContactsContract.CommonDataKinds.Phone.TYPE

        val placeholders = lookupKeys.joinToString(",") { "?" }
        val selection = "${ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY} IN ($placeholders)"

        return queryContentResolver(
            uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection = arrayOf(
                lookupKeyColumn, displayNameColumn, photoUriColumn, phoneNumberColumn,
                phoneTypeColumn
            ),
            selection = selection,
            selectionArgs = lookupKeys.toTypedArray()
        ) { cursor ->
            val lookupKeyIndex = cursor.getColumnIndexOrThrow(lookupKeyColumn)
            val displayNameIndex = cursor.getColumnIndexOrThrow(displayNameColumn)
            val photoUriIndex = cursor.getColumnIndexOrThrow(photoUriColumn)
            val phoneNumberIndex = cursor.getColumnIndexOrThrow(phoneNumberColumn)
            val phoneTypeIndex = cursor.getColumnIndexOrThrow(phoneTypeColumn)

            // Map from lookup keys to Contacts
            val contacts: MutableMap<String, Contact> = mutableMapOf()

            while (cursor.moveToNext()) {
                val lookupKey = cursor.getString(lookupKeyIndex) ?: continue
                val displayName = cursor.getString(displayNameIndex).orEmpty()
                val photoUri = cursor.getString(photoUriIndex)
                val phoneNumber = cursor.getString(phoneNumberIndex) ?: continue
                val phoneType = cursor.getInt(phoneTypeIndex).toPhoneType()

                val phone = Phone(phoneNumber, phoneType)

                val existingContact = contacts[lookupKey]
                if (existingContact != null) {
                    contacts[lookupKey] = existingContact.copy(
                        phones = existingContact.phones + phone
                    )
                } else {
                    contacts[lookupKey] = Contact(
                        displayName = displayName,
                        pictureUri = photoUri,
                        phones = listOf(phone)
                    )
                }
            }

            val contactList = contacts.values.toList()
            Result.success(contactList)
        }
    }

    private suspend fun <T> queryContentResolver(
        uri: Uri,
        projection: Array<String>,
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        onCursor: (Cursor) -> Result<T>
    ): Result<T> {
        return try {
            withContext(ioDispatcher) {
                contentResolver.query(uri, projection, selection, selectionArgs, null)
                    ?.use(onCursor)
                    ?: Result.failure(IOException("Query returned null cursor"))
            }
        } catch (e: SecurityException) {
            Result.failure(e)
        }
    }
}

private fun Int.toPhoneType() = when (this) {
    ContactsContract.CommonDataKinds.Phone.TYPE_HOME,
    ContactsContract.CommonDataKinds.Phone.TYPE_MAIN -> PhoneType.HOME
    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
    ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE -> PhoneType.MOBILE
    else -> PhoneType.UNKNOWN
}
