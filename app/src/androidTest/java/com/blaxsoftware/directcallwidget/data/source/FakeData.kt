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
import androidx.core.net.toUri
import dev.ferp.dcw.data.contacts.Contact
import dev.ferp.dcw.data.contacts.Phone
import com.blaxsoftware.directcallwidget.data.SingleContactWidget

val fakeWidgetData = SingleContactWidget(
        widgetId = 3,
        displayName = "Alice",
        phoneNumber = "+34 123",
        phoneType = CommonDataKinds.Phone.TYPE_HOME,
        pictureUri = "content://alice.jpg"
)

val fakeContact = dev.ferp.dcw.data.contacts.Contact(
    "Alice",
    "content://alice.jpg".toUri(),
    listOf(
        dev.ferp.dcw.data.contacts.Phone("+34 123", CommonDataKinds.Phone.TYPE_HOME),
        dev.ferp.dcw.data.contacts.Phone("+34 124", CommonDataKinds.Phone.TYPE_MOBILE),
        dev.ferp.dcw.data.contacts.Phone("+34 125", CommonDataKinds.Phone.TYPE_OTHER)
    )
)
