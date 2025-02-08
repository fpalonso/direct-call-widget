package dev.ferp.dcw.data.phones.source.device

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.ferp.dcw.data.phones.Phone
import dev.ferp.dcw.data.phones.PhoneType
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CursorPhoneListAdapterTest {

    @Test
    fun `toPhoneList returns phones from cursor`() {
        // Given
        val cursor = TestPhoneProvider.phoneListCursor()

        // When
        val phones = cursor.toPhoneList()

        // Then
        assertThat(phones).contains(Phone("123", PhoneType.MOBILE))
        assertThat(phones).contains(Phone("456", PhoneType.HOME))
        assertThat(phones).contains(Phone("789", PhoneType.UNKNOWN))
    }

    @Test
    fun `toPhoneList returns empty list from empty cursor`() {
        assertThat(TestPhoneProvider.emptyPhoneListCursor().toPhoneList())
            .isEmpty()
    }

    @Test
    fun `toPhoneList does not return blank numbers from cursor`() {
        // Given
        val cursor = TestPhoneProvider.phoneListCursor()

        // When
        val phones = cursor.toPhoneList()

        // Then
        val trimmedNumbers = phones.map { it.number.trim() }
        assertThat(trimmedNumbers).doesNotContain("")
    }
}