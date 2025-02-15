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
import android.provider.ContactsContract.CommonDataKinds
import dev.ferp.dcw.core.androidutil.getIntOrNull
import dev.ferp.dcw.core.androidutil.getStringOrNull
import dev.ferp.dcw.data.phones.Phone
import dev.ferp.dcw.data.phones.PhoneType

/**
 * Converts a cursor with [CommonDataKinds.Phone.NUMBER] and [CommonDataKinds.Phone.TYPE]
 * into a [Phone] list.
 *
 * @return a phone list, which may be null.
 */
internal fun Cursor.toPhoneList(): List<Phone> {
    val list = mutableListOf<Phone>()
    while (moveToNext()) {
        val number = getStringOrNull(CommonDataKinds.Phone.NUMBER) ?: ""
        if (number.isBlank()) continue
        list.add(
            Phone(
                number = number,
                type = getPhoneType(getIntOrNull(CommonDataKinds.Phone.TYPE))
            )
        )
    }
    return list
}

/** Converts a device phone type into a [PhoneType]. */
private fun getPhoneType(devicePhoneType: Int?): PhoneType = when (devicePhoneType) {
    CommonDataKinds.Phone.TYPE_MOBILE -> PhoneType.MOBILE
    CommonDataKinds.Phone.TYPE_HOME -> PhoneType.HOME
    else -> PhoneType.UNKNOWN
}