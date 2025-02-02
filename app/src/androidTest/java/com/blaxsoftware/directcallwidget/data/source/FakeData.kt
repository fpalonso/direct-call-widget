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

import android.provider.ContactsContract.CommonDataKinds
import com.blaxsoftware.directcallwidget.data.SingleContactWidget
import dev.ferp.dcw.data.contacts.Contact
import dev.ferp.dcw.data.contacts.Contact.PhoneType

val fakeWidgetData = SingleContactWidget(
        widgetId = 3,
        displayName = "Alice",
        phoneNumber = "+34 123",
        phoneType = CommonDataKinds.Phone.TYPE_HOME,
        pictureUri = "content://alice.jpg"
)

val fakeContact = Contact(
    "Alice",
    "content://alice.jpg",
    listOf(
        Contact.Phone("+34 123", PhoneType.HOME),
        Contact.Phone("+34 124", PhoneType.MOBILE),
        Contact.Phone("+34 125", PhoneType.UNKNOWN)
    )
)
