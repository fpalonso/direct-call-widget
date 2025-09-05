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

package dev.ferp.dcw.core.contactprovider

import android.content.ContentResolver
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds
import android.provider.ContactsContract.Contacts
import android.util.Log
import androidx.core.net.toUri
import dev.ferp.dcw.core.contactprovider.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

internal class ProviderContactDataSource @Inject constructor(
    private val contentResolver: ContentResolver,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : LocalDataSource {

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
                        displayName = cursor.getString(Contacts.DISPLAY_NAME).orEmpty(),
                        pictureUri = cursor.getString(Contacts.PHOTO_URI),
                        phones = getContactPhones(cursor.getString(Contacts.LOOKUP_KEY).orEmpty())
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
                "${CommonDataKinds.Phone.LOOKUP_KEY} = ? AND ${CommonDataKinds.Phone.MIMETYPE} = ?",
                arrayOf(lookUpKey, CommonDataKinds.Phone.CONTENT_ITEM_TYPE),
                null
            )?.use { cursor ->
                val list = mutableListOf<LocalPhone>()
                while (cursor.moveToNext()) {
                    Log.d("fer", "getContactPhones: cursor has next")
                    cursor.getString(CommonDataKinds.Phone.NUMBER)?.let { number ->
                        list.add(
                            LocalPhone(
                                number = number,
                                type = getPhoneType(cursor.getInt(CommonDataKinds.Phone.TYPE))
                            )
                        )
                    }
                }
                Log.d("fer", "getContactPhones: returning $list")
                list
            } ?: emptyList()
        }
    }

    /** Converts a device phone type into a [LocalPhoneType]. */
    private fun getPhoneType(devicePhoneType: Int?): LocalPhoneType = when (devicePhoneType) {
        CommonDataKinds.Phone.TYPE_MOBILE -> LocalPhoneType.MOBILE
        CommonDataKinds.Phone.TYPE_HOME -> LocalPhoneType.HOME
        else -> LocalPhoneType.UNKNOWN
    }
}

