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

package dev.ferp.dcw.data.devicecontact.source.local

import android.content.ContentResolver
import android.provider.ContactsContract
import androidx.core.net.toUri
import dev.ferp.dcw.core.di.IoDispatcher
import dev.ferp.dcw.data.devicecontact.getInt
import dev.ferp.dcw.data.devicecontact.getString
import dev.ferp.dcw.data.devicecontact.source.DeviceContactDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

internal class DeviceContactProviderDataSource @Inject constructor(
    private val contentResolver: ContentResolver,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : DeviceContactDataSource {

    override suspend fun getDeviceContactByUri(contactUri: String): Result<LocalContact> {
        return withContext(ioDispatcher) {
            contentResolver.query(
                contactUri.toUri(), ProviderContract.CONTACT_PROJECTION, null, null, null
            )?.use { cursor ->
                if (!cursor.moveToFirst()) {
                    return@use Result.failure(IllegalArgumentException("Contact not found"))
                }
                Result.success(
                    LocalContact(
                        displayName = cursor.getString(ContactsContract.Contacts.DISPLAY_NAME)
                            .orEmpty(),
                        pictureUri = cursor.getString(ContactsContract.Contacts.PHOTO_URI),
                        phones = getContactPhones(
                            cursor.getString(ContactsContract.Contacts.LOOKUP_KEY).orEmpty()
                        )
                    )
                )
            } ?: Result.failure(IOException("Unable to read contacts"))
        }
    }

    private suspend fun getContactPhones(lookUpKey: String): List<LocalPhone> {
        return withContext(ioDispatcher) {
            contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                ProviderContract.PHONE_PROJECTION,
                "${ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY} = ? AND ${ContactsContract.CommonDataKinds.Phone.MIMETYPE} = ?",
                arrayOf(lookUpKey, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE),
                null
            )?.use { cursor ->
                val list = mutableListOf<LocalPhone>()
                while (cursor.moveToNext()) {
                    cursor.getString(ContactsContract.CommonDataKinds.Phone.NUMBER)?.let { number ->
                        list.add(
                            LocalPhone(
                                number = number,
                                type = getPhoneType(cursor.getInt(ContactsContract.CommonDataKinds.Phone.TYPE))
                            )
                        )
                    }
                }
                list
            } ?: emptyList()
        }
    }

    /** Converts a device phone type into a [LocalPhoneType]. */
    private fun getPhoneType(devicePhoneType: Int?): LocalPhoneType = when (devicePhoneType) {
        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE -> LocalPhoneType.MOBILE
        ContactsContract.CommonDataKinds.Phone.TYPE_HOME -> LocalPhoneType.HOME
        else -> LocalPhoneType.UNKNOWN
    }
}

internal object ProviderContract {

    /** Projection to use for querying a contact */
    val CONTACT_PROJECTION = arrayOf(
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.Contacts.PHOTO_URI,
        ContactsContract.Contacts.LOOKUP_KEY
    )

    /** Projection for retrieving a contact list of phones */
    val PHONE_PROJECTION = arrayOf(
        ContactsContract.CommonDataKinds.Phone.NUMBER,
        ContactsContract.CommonDataKinds.Phone.TYPE
    )
}