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

package dev.ferp.dcw.data.contacts

import android.database.Cursor
import android.database.MatrixCursor
import android.provider.ContactsContract.Contacts
import androidx.core.net.toUri

object TestContactProvider {

    val CONTACT_URI = "content://contacts/2".toUri()
    private const val DISPLAY_NAME = "Alice"
    private const val PHOTO_URI = "content://dev.ferp.dcw/pics/alice.jpg"
    private const val LOOKUP_KEY = "someLookUpKey"

    private val CONTACT_PROJECTION = arrayOf(
        Contacts.DISPLAY_NAME, Contacts.PHOTO_URI, Contacts.LOOKUP_KEY
    )

    fun contactCursor(): Cursor {
        val cursor = MatrixCursor(CONTACT_PROJECTION)
        cursor.addRow(arrayOf(DISPLAY_NAME, PHOTO_URI, LOOKUP_KEY))
        return cursor
    }

    fun emptyContactCursor(): Cursor = MatrixCursor(CONTACT_PROJECTION)

    fun contact(): Contact = Contact(
        displayName = DISPLAY_NAME,
        photoUri = PHOTO_URI,
        lookUpKey = LOOKUP_KEY
    )
}