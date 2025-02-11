package dev.ferp.dcw.data.onecontactwidget

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.ferp.dcw.data.contacts.OneContactWidget
import dev.ferp.dcw.data.contacts.OneContactWidgetRepository
import dev.ferp.dcw.data.onecontactwidget.source.sharedprefs.FakeSharedPrefDataSource
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DefaultOneContactWidgetRepositoryTest {

    private val widget = OneContactWidget(
        appWidgetId = 1,
        displayName = "John Doe",
        phoneNumber = "1234567890",
        phoneType = 1,
        pictureUri = "content://contacts/1"
    )

    private lateinit var repository: OneContactWidgetRepository

    @Before
    fun setUp() {
        repository = DefaultOneContactWidgetRepository(
            sharedPrefsDataSource = FakeSharedPrefDataSource()
        )
    }

    @Test
    fun `createWidget and getWidget`() = runTest {
        // Given
        repository.createWidget(
            appWidgetId = 1,
            displayName = "John Doe",
            phoneNumber = "1234567890",
            phoneType = 1,
            pictureUri = "content://contacts/1"
        )

        // When
        val result = repository.getWidget(1)

        // Then
        assertThat(result).isEqualTo(widget)
    }

    @Test
    fun `deleteWidget deletes a widget data`() = runTest {
        // Given
        repository.createWidget(
            appWidgetId = 1,
            displayName = "John Doe",
            phoneNumber = "1234567890",
            phoneType = 1,
            pictureUri = "content://contacts/1"
        )
        assertThat(repository.getWidget(1)).isNotNull()

        // When
        val existed = repository.deleteWidget(1)

        // Then
        assertThat(existed).isTrue()
        assertThat(repository.getWidget(1)).isNull()
    }
}