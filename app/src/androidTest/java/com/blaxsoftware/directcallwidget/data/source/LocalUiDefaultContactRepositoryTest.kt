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

import android.content.ContentResolver
import android.database.MatrixCursor
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER
import android.provider.ContactsContract.CommonDataKinds.Phone.TYPE
import android.provider.ContactsContract.Contacts
import androidx.core.net.toUri
import com.google.common.truth.Truth
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

class LocalUiDefaultContactRepositoryTest {

    private val mockResolver = mockk<ContentResolver>()

    private val fakeContactCursor: MatrixCursor
        get() = MatrixCursor(arrayOf(Contacts.DISPLAY_NAME, Contacts.PHOTO_URI, Contacts.LOOKUP_KEY))

    private val fakePhoneCursor: MatrixCursor
        get() = MatrixCursor(arrayOf(NUMBER, TYPE))

    private val repository = DefaultContactRepository(mockResolver)

    @Test
    fun getContactByUri_dataFound_returnsIt() {
        every {
            mockResolver.query(any(), any(), any(), any(), any())
        } returns fakeContactCursor.apply {
            with(fakeContact) {
                addRow(arrayOf(displayName, photoUri, "lookupKey"))
            }
        }
        every {
            mockResolver.query(ContactsContract.Data.CONTENT_URI, any(), any(), any(), any())
        } returns fakePhoneCursor.apply {
            fakeContact.phoneList.forEach {
                addRow(arrayOf(it.number, it.type))
            }
        }
        val expectedResult = fakeContact

        runBlocking {
            val contactList = repository.getContactById("content://contacts/alice".toUri())

            Truth.assertThat(contactList).isEqualTo(expectedResult)
        }
    }

    @Test
    fun getContactByUri_noPhones_returnsContactWithEmptyPhoneList() {
        every {
            mockResolver.query(any(), any(), any(), any(), any())
        } returns fakeContactCursor.apply {
            with(fakeContact) {
                addRow(arrayOf(displayName, photoUri, "lookupKey"))
            }
        }
        every {
            mockResolver.query(ContactsContract.Data.CONTENT_URI, any(), any(), any(), any())
        } returns fakePhoneCursor
        val expectedResult = with(fakeContact) {
            dev.ferp.dcw.data.contacts.Contact(
                displayName,
                photoUri,
                emptyList()
            )
        }

        runBlocking {
            val contactList = repository.getContactById("content://contacts/alice".toUri())

            Truth.assertThat(contactList).isEqualTo(expectedResult)
        }
    }

    @Test
    fun getContactByUri_errorFetchingPhones_returnsContactWithEmptyPhoneList() {
        every {
            mockResolver.query(any(), any(), any(), any(), any())
        } returns fakeContactCursor.apply {
            with(fakeContact) {
                addRow(arrayOf(displayName, photoUri, "lookupKey"))
            }
        }
        every {
            mockResolver.query(ContactsContract.Data.CONTENT_URI, any(), any(), any(), any())
        } returns null
        val expectedResult = with(fakeContact) {
            dev.ferp.dcw.data.contacts.Contact(
                displayName,
                photoUri,
                emptyList()
            )
        }

        runBlocking {
            val contactList = repository.getContactById("content://contacts/alice".toUri())

            Truth.assertThat(contactList).isEqualTo(expectedResult)
        }
    }

    @Test
    fun getContactByUri_displayNameIsNull_returnsContactWithEmptyName() {
        every {
            mockResolver.query(any(), any(), any(), any(), any())
        } returns fakeContactCursor.apply {
            with(fakeContact) {
                addRow(arrayOf(displayName, photoUri, "lookupKey"))
            }
        }
        every {
            mockResolver.query(ContactsContract.Data.CONTENT_URI, any(), any(), any(), any())
        } returns fakePhoneCursor
        val expectedResult = with(fakeContact) {
            dev.ferp.dcw.data.contacts.Contact(
                displayName,
                photoUri,
                emptyList()
            )
        }

        runBlocking {
            val contactList = repository.getContactById("content://contacts/alice".toUri())

            Truth.assertThat(contactList).isEqualTo(expectedResult)
        }
    }

    @Test
    fun getContactByUri_notFound_returnsNull() {
        every {
            mockResolver.query(any(), any(), any(), any(), any())
        } returns fakeContactCursor
        val expectedResult = null

        runBlocking {
            val contactList = repository.getContactById("content://contacts/alice".toUri())

            Truth.assertThat(contactList).isEqualTo(expectedResult)
        }
    }

    @Test
    fun getContactByUri_errorFetchingContact_returnsNull() {
        every {
            mockResolver.query(any(), any(), any(), any(), any())
        } returns null
        val expectedResult = null

        runBlocking {
            val contactList = repository.getContactById("content://contacts/alice".toUri())

            Truth.assertThat(contactList).isEqualTo(expectedResult)
        }
    }
}
