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
import androidx.core.net.toUri
import com.google.common.truth.Truth
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

// TODO replace with a Robolectric test
class LocalUiDefaultContactRepositoryTest {

    private val mockResolver = mockk<ContentResolver>()
    private val repository = DefaultContactRepository(mockResolver)

    @Test
    fun getContactByUri_dataFound_returnsIt() = runTest {
        // Given
        every {
            mockResolver.query(any(), any(), any(), any(), any())
        } returns TestContact.contactCursor()

        // When
        val contact = repository.getContactById("content://contacts/alice".toUri())

        // Then
        val expectedContact = TestContact.contact()
        Truth.assertThat(contact).isEqualTo(expectedContact)
    }

    @Test
    fun getContactByUri_displayNameIsNull_returnsContactWithEmptyName() = runTest {
        // Given
        every {
            mockResolver.query(any(), any(), any(), any(), any())
        } returns TestContact.contactWithEmptyNameCursor()

        // When
        val contact = repository.getContactById("content://contacts/alice".toUri())

        // Then
        val expectedContact = TestContact.contactWithEmptyName()
        Truth.assertThat(contact).isEqualTo(expectedContact)
    }

    @Test
    fun getContactByUri_notFound_returnsNull() = runTest {
        // Given
        every {
            mockResolver.query(any(), any(), any(), any(), any())
        } returns TestContact.emptyContactCursor()

        // When
        val contact = repository.getContactById("content://contacts/alice".toUri())

        // Then
        Truth.assertThat(contact).isNull()
    }

    @Test
    fun getContactByUri_errorFetchingContact_returnsNull() = runTest {
        // Given
        every {
            mockResolver.query(any(), any(), any(), any(), any())
        } returns null

        // When
        val contact = repository.getContactById("content://contacts/alice".toUri())

        // Then
        Truth.assertThat(contact).isNull()
    }
}
