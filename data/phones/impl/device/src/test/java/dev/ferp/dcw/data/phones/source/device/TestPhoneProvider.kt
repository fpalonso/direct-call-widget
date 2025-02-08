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

import android.database.Cursor
import android.database.MatrixCursor
import android.provider.ContactsContract.CommonDataKinds
import dev.ferp.dcw.data.phones.Phone
import dev.ferp.dcw.data.phones.PhoneType

object TestPhoneProvider {

    private val CONTACT_PROJECTION = arrayOf(
        CommonDataKinds.Phone.NUMBER, CommonDataKinds.Phone.TYPE
    )

    fun phoneListCursor(): Cursor {
        val cursor = MatrixCursor(CONTACT_PROJECTION)
        cursor.addRow(arrayOf("123", CommonDataKinds.Phone.TYPE_MOBILE))
        cursor.addRow(arrayOf("   ", CommonDataKinds.Phone.TYPE_MOBILE))
        cursor.addRow(arrayOf("456", CommonDataKinds.Phone.TYPE_HOME))
        cursor.addRow(arrayOf("789", CommonDataKinds.Phone.TYPE_CAR))
        return cursor
    }

    fun emptyPhoneListCursor(): Cursor {
        return MatrixCursor(CONTACT_PROJECTION)
    }

    fun nonBlankNumbersPhoneList(): List<Phone> {
        return listOf(
            Phone(number = "123", type = PhoneType.MOBILE),
            Phone(number = "456", type = PhoneType.HOME),
            Phone(number = "789", type = PhoneType.UNKNOWN),
        )
    }
}