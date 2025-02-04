package dev.ferp.dcw.data.contacts

import android.content.ContentResolver
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.ferp.dcw.core.util.test.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.fail
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DefaultContactRepositoryTest {

    private val testDispatcher: TestDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    private lateinit var contentResolver: ContentResolver
    private lateinit var repo: ContactRepository<Uri>

    @Before
    fun setUp() {
        contentResolver = mockk()
        repo = DefaultContactRepository(
            contentResolver = contentResolver,
            ioDispatcher = testDispatcher
        )
    }

    @Test
    fun `getContactById returns contact`() = runTest {
        // Given
        every {
            contentResolver.query(TestContactProvider.CONTACT_URI, any(), any(), any(), any())
        } returns TestContactProvider.contactCursor()

        // When
        val contact = repo.getContactById(TestContactProvider.CONTACT_URI)

        // Then
        val expectedContact = TestContactProvider.contact()
        assertThat(contact).isEqualTo(expectedContact)
    }

    @Test
    fun `getContactById returns null when cursor is empty`() = runTest {
        // Given
        every {
            contentResolver.query(TestContactProvider.CONTACT_URI, any(), any(), any(), any())
        } returns TestContactProvider.emptyContactCursor()

        // When
        val contact = repo.getContactById(TestContactProvider.CONTACT_URI)

        // Then
        assertThat(contact).isNull()
    }

    @Test
    fun `getContactById returns null when cursor is null`() = runTest {
        // Given
        every {
            contentResolver.query(TestContactProvider.CONTACT_URI, any(), any(), any(), any())
        } returns null

        // When
        val contact = repo.getContactById(TestContactProvider.CONTACT_URI)

        // Then
        assertThat(contact).isNull()
    }

    @Test
    fun `getContactById propagates exception`() = runTest {
        // Given
        every {
            contentResolver.query(TestContactProvider.CONTACT_URI, any(), any(), any(), any())
        } throws IOException()

        // When
        try {
            repo.getContactById(TestContactProvider.CONTACT_URI)
            fail("Exception expected")
        } catch (e: Throwable) {
            assertThat(e).isInstanceOf(IOException::class.java)
        }
    }
}