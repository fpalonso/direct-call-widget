package dev.ferp.dcw.data.phones.source.device

import android.content.ContentResolver
import android.provider.ContactsContract
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.ferp.dcw.core.util.test.MainDispatcherRule
import dev.ferp.dcw.data.phones.source.PhoneDataSource
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PhoneDeviceDataSourceTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    private lateinit var contentResolver: ContentResolver
    private lateinit var dataSource: PhoneDataSource

    @Before
    fun setUp() {
        contentResolver = mockk()
        dataSource = PhoneDeviceDataSource(
            contentResolver = contentResolver,
            ioDispatcher = testDispatcher
        )
    }

    @Test
    fun `getPhoneList returns list of phones`() = runTest {
        // Given
        every {
            contentResolver.query(ContactsContract.Data.CONTENT_URI, any(), any(), any(), any())
        } returns TestPhoneProvider.phoneListCursor()

        // When
        val phones = dataSource.getPhoneList("lookUpKey")

        // Then
        assertThat(phones).isEqualTo(TestPhoneProvider.nonBlankNumbersPhoneList())
    }

    @Test
    fun `getPhoneList returns empty list for empty cursor`() = runTest {
        // Given
        every {
            contentResolver.query(ContactsContract.Data.CONTENT_URI, any(), any(), any(), any())
        } returns TestPhoneProvider.emptyPhoneListCursor()

        // When
        val phones = dataSource.getPhoneList("lookUpKey")

        // Then
        assertThat(phones).isEmpty()
    }
}