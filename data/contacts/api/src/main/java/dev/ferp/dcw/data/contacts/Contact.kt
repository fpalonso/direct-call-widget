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

/** Contact information from the device */
data class Contact(
    /** Displayed contact name */
    val displayName: String,
    /** Uri for the contact picture */
    val photoUri: String?,
    /** List of phones owned by this contact */
    val phoneList: List<Phone>
) {
    enum class PhoneType {
        HOME, MOBILE, UNKNOWN
    }

    data class Phone(
        val number: String,
        val type: PhoneType
    )
}
