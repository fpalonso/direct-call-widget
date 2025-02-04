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

    fun emptyPhonesCursor(): MatrixCursor {
        val columns = arrayOf(Phone.NUMBER, Phone.TYPE)
        val cursor = MatrixCursor(columns)
        return cursor
    }

    fun contact(): Contact = Contact(
        displayName = CONTACT_NAME,
        photoUri = CONTACT_PHOTO_URI,
        lookUpKey = CONTACT_LOOKUP_KEY
    )

    fun contactWithEmptyName(): Contact = Contact(
        displayName = "",
        photoUri = CONTACT_PHOTO_URI,
        lookUpKey = CONTACT_LOOKUP_KEY
    )
}