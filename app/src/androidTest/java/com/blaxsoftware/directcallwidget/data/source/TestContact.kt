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

import android.database.MatrixCursor
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.Contacts
import com.blaxsoftware.directcallwidget.data.SingleContactWidget
import com.blaxsoftware.directcallwidget.phoneTypeFromCommonDataKinds
import dev.ferp.dcw.data.contacts.Contact

val fakeWidgetData = SingleContactWidget(
    widgetId = 3,
    displayName = "Alice",
    phoneNumber = "+34 123",
    phoneType = Phone.TYPE_HOME,
    pictureUri = "content://alice.jpg"
)

object TestContact {

    private const val CONTACT_NAME = "Alice"
    private const val CONTACT_PHOTO_URI = "content://alice.jpg"
    private const val CONTACT_LOOKUP_KEY = "lookupKey"
    private val COMMON_DATA_KINDS_PHONES = listOf(
        "+34 123" to Phone.TYPE_HOME,
        "+34 124" to Phone.TYPE_MOBILE,
        "+34 125" to Phone.TYPE_OTHER
    )
    private val CONTACT_PHONES = COMMON_DATA_KINDS_PHONES
        .map { phone ->
            Contact.Phone(
                number = phone.first,
                type = phoneTypeFromCommonDataKinds(phone.second)
            )
        }

    fun contactCursor(): MatrixCursor {
        val cursor = emptyContactCursor()
        cursor.addRow(arrayOf(CONTACT_NAME, CONTACT_PHOTO_URI, CONTACT_LOOKUP_KEY))
        return cursor
    }

    fun contactWithEmptyNameCursor(): MatrixCursor {
        val cursor = emptyContactCursor()
        cursor.addRow(arrayOf(null, CONTACT_PHOTO_URI, CONTACT_LOOKUP_KEY))
        return cursor
    }

    fun emptyContactCursor(): MatrixCursor {
        val columns = arrayOf(Contacts.DISPLAY_NAME, Contacts.PHOTO_URI, Contacts.LOOKUP_KEY)
        val cursor = MatrixCursor(columns)
        return cursor
    }

    fun phonesCursor(): MatrixCursor {
        val cursor = emptyPhonesCursor()
        COMMON_DATA_KINDS_PHONES.forEach { phone ->
            cursor.addRow(arrayOf(phone.first, phone.second))
        }
        return cursor
    }

    fun emptyPhonesCursor(): MatrixCursor {
        val columns = arrayOf(Phone.NUMBER, Phone.TYPE)
        val cursor = MatrixCursor(columns)
        return cursor
    }

    fun contact(): Contact = Contact(
        displayName = CONTACT_NAME,
        photoUri = CONTACT_PHOTO_URI,
        phoneList = CONTACT_PHONES
    )

    fun contactWithEmptyName(): Contact = Contact(
        displayName = "",
        photoUri = CONTACT_PHOTO_URI,
        phoneList = CONTACT_PHONES
    )

    fun contactWithEmptyPhoneList(): Contact = Contact(
        displayName = CONTACT_NAME,
        photoUri = CONTACT_PHOTO_URI,
        phoneList = emptyList()
    )
}