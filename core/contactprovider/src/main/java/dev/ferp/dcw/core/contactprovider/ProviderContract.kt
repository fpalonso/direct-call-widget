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

import android.provider.ContactsContract.CommonDataKinds
import android.provider.ContactsContract.Contacts

internal object ProviderContract {

    /** Projection to use for querying a contact */
    val CONTACT_PROJECTION = arrayOf(
        Contacts.DISPLAY_NAME,
        Contacts.PHOTO_URI,
        Contacts.LOOKUP_KEY
    )

    /** Projection for retrieving a contact list of phones */
    val PHONE_PROJECTION = arrayOf(
        CommonDataKinds.Phone.NUMBER,
        CommonDataKinds.Phone.TYPE
    )
}