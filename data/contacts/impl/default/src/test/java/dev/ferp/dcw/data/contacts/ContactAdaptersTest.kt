package dev.ferp.dcw.data.contacts

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContactAdaptersTest {

    @Test
    fun `toContact returns a contact from a cursor`() {
        assertThat(TestContactProvider.contactCursor().toContact())
            .isEqualTo(TestContactProvider.contact())
    }

    @Test
    fun `toContact returns null from an empty cursor`() {
        assertThat(TestContactProvider.emptyContactCursor().toContact())
            .isNull()
    }
}