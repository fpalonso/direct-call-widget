package com.blaxsoftware.directcallwidget.data.source

import android.content.ContentResolver
import android.database.MatrixCursor
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER
import android.provider.ContactsContract.CommonDataKinds.Phone.TYPE
import android.provider.ContactsContract.Contacts
import androidx.core.net.toUri
import com.blaxsoftware.directcallwidget.data.model.Contact
import com.google.common.truth.Truth
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ContactRepositoryTest {

    private val mockResolver = mockk<ContentResolver>()

    private val fakeContactCursor: MatrixCursor
        get() = MatrixCursor(arrayOf(Contacts.DISPLAY_NAME, Contacts.PHOTO_URI, Contacts.LOOKUP_KEY))

    private val fakePhoneCursor: MatrixCursor
        get() = MatrixCursor(arrayOf(NUMBER, TYPE))

    private val repository = ContactRepository(mockResolver)

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
            val contactList = repository.getContactByUri("content://contacts/alice".toUri())

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
            Contact(
                    displayName,
                    photoUri,
                    emptyList()
            )
        }

        runBlocking {
            val contactList = repository.getContactByUri("content://contacts/alice".toUri())

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
            Contact(
                    displayName,
                    photoUri,
                    emptyList()
            )
        }

        runBlocking {
            val contactList = repository.getContactByUri("content://contacts/alice".toUri())

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
            Contact(
                    displayName,
                    photoUri,
                    emptyList()
            )
        }

        runBlocking {
            val contactList = repository.getContactByUri("content://contacts/alice".toUri())

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
            val contactList = repository.getContactByUri("content://contacts/alice".toUri())

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
            val contactList = repository.getContactByUri("content://contacts/alice".toUri())

            Truth.assertThat(contactList).isEqualTo(expectedResult)
        }
    }
}
