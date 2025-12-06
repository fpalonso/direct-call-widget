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

package dev.ferp.dcw.data.onecontactwidget.source.local

import android.provider.ContactsContract

internal data class LocalOneContactWidget(
    val appWidgetId: Int,
    val displayName: String?,
    val phoneNumber: String,
    /** One of the values in [LocalPhoneType] */
    val phoneType: Int?,
    val pictureUri: String?
)

internal object LocalPhoneType {
    const val HOME = ContactsContract.CommonDataKinds.Phone.TYPE_HOME
    const val MOBILE = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
    val UNKNOWN = null
}