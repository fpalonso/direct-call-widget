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

package dev.ferp.dcw.data.devicecontact

import dev.ferp.dcw.core.domain.data.PhoneType
import dev.ferp.dcw.data.devicecontact.source.DeviceContactDataSource
import dev.ferp.dcw.data.devicecontact.source.local.LocalContact
import dev.ferp.dcw.data.devicecontact.source.local.LocalPhone
import dev.ferp.dcw.data.devicecontact.source.local.LocalPhoneType
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DefaultDeviceContactRepositoryTest {

    private lateinit var dataSource: DeviceContactDataSource
    private lateinit var repository: DefaultDeviceContactRepository

    @Before
    fun init() {
        dataSource = mockk()
        repository = DefaultDeviceContactRepository(dataSource)
    }

    @Test
    fun `Get contact returns valid contact`() = runTest {
        // Given
        coEvery {
            dataSource.getDeviceContactByUri(any())
        } returns Result.success(
            LocalContact(
                displayName = "Test contact",
                pictureUri = "content://picture/1",
                phones = listOf(
                    LocalPhone(
                        number = "123456789",
                        type = LocalPhoneType.HOME
                    )
                )
            )
        )

        // When
        val result = repository.getContactByUri("content://contacts/1")
        val contact = result.getOrNull()

        // Then
        assertTrue(result.isSuccess)
        assertEquals("Test contact", contact?.displayName)
        assertEquals("content://picture/1", contact?.pictureUri)
        assertEquals(1, contact?.phones?.size)
        assertEquals("123456789", contact?.phones?.get(0)?.number)
        assertEquals(PhoneType.HOME, contact?.phones?.get(0)?.type)
    }

    @Test
    fun `Get contact returns null for non-present contact`() = runTest {
        // Given
        coEvery {
            dataSource.getDeviceContactByUri(any())
        } returns Result.failure(Throwable("Contact not found"))

        // When
        val result = repository.getContactByUri("content://contacts/1")
        val contact = result.getOrNull()

        // Then
        assertTrue(result.isFailure)
        assertNull(contact)
    }
}