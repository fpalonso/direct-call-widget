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

package dev.ferp.dcw.data.devicecontact.source.local

import android.content.ContentResolver
import android.database.MatrixCursor
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.ferp.dcw.data.devicecontact.source.DeviceContactDataSource
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeviceContactProviderDataSourceTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var contentResolver: ContentResolver
    private lateinit var dataSource: DeviceContactDataSource

    @Before
    fun init() {
        contentResolver = mockk()
        dataSource = DeviceContactProviderDataSource(contentResolver, testDispatcher)
    }

    @Test
    fun `Get contact with two phones`() = runTest(testDispatcher) {
        // Given
        every {
            contentResolver.query(any(), ProviderContract.CONTACT_PROJECTION, any(), any(), any())
        } returns MatrixCursor(ProviderContract.CONTACT_PROJECTION).apply {
            addRow(
                arrayOf("Test contact", "content://picture/1", "lookupKey")
            )
        }
        every {
            contentResolver.query(any(), ProviderContract.PHONE_PROJECTION, any(), any(), any())
        } returns MatrixCursor(ProviderContract.PHONE_PROJECTION).apply {
            addRow(arrayOf("123456789", "1"))
            addRow(arrayOf("987654321", "2"))
        }

        // When
        val result = dataSource.getDeviceContactByUri("content://contacts/1")
        val contact = result.getOrNull()

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(contact?.displayName).isEqualTo("Test contact")
        assertThat(contact?.pictureUri).isEqualTo("content://picture/1")
        assertThat(contact?.phones).hasSize(2)
        assertThat(contact?.phones?.get(0)?.number).isEqualTo("123456789")
        assertThat(contact?.phones?.get(0)?.type).isEqualTo(LocalPhoneType.HOME)
        assertThat(contact?.phones?.get(1)?.number).isEqualTo("987654321")
        assertThat(contact?.phones?.get(1)?.type).isEqualTo(LocalPhoneType.MOBILE)
    }

    @Test
    fun `Get contact fails when cursor is empty`() = runTest(testDispatcher) {
        // Given
        every {
            contentResolver.query(any(), ProviderContract.CONTACT_PROJECTION, any(), any(), any())
        } returns MatrixCursor(ProviderContract.CONTACT_PROJECTION)

        // When
        val result = dataSource.getDeviceContactByUri("content://contacts/1")

        // Then
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `Get contact fails when cursor is null`() = runTest(testDispatcher) {
        // Given
        every {
            contentResolver.query(any(), ProviderContract.CONTACT_PROJECTION, any(), any(), any())
        } returns null

        // When
        val result = dataSource.getDeviceContactByUri("content://contacts/1")

        // Then
        assertThat(result.isFailure).isTrue()
    }
}