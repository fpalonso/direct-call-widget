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

package dev.ferp.dcw.data.phones.source.device

import android.content.ContentResolver
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds
import dev.ferp.dcw.core.di.IoDispatcher
import dev.ferp.dcw.data.phones.Phone
import dev.ferp.dcw.data.phones.source.PhoneDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PhoneDeviceDataSource @Inject constructor(
    private val contentResolver: ContentResolver,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : PhoneDataSource {

    override suspend fun getPhoneList(lookUpKey: String): List<Phone> = withContext(ioDispatcher){
        val cursor = contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            PHONE_PROJECTION,
            "${CommonDataKinds.Phone.LOOKUP_KEY} = ? AND ${CommonDataKinds.Phone.MIMETYPE} = ?",
            arrayOf(lookUpKey, CommonDataKinds.Phone.CONTENT_ITEM_TYPE),
            null
        )
        cursor
            ?.use { it.toPhoneList() }
            ?: emptyList()
    }

    companion object {

        /** Projection for retrieving a contact list of phones */
        private val PHONE_PROJECTION = arrayOf(
            CommonDataKinds.Phone.NUMBER,
            CommonDataKinds.Phone.TYPE
        )
    }
}