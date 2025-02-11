package dev.ferp.dcw.data.onecontactwidget.source.sharedprefs

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OneContactWidgetPrefDSTest {

    private val widget = SharedPrefWidget(
        appWidgetId = 1,
        displayName = "John Doe",
        phoneNumber = "123456789",
        phoneType = 1,
        pictureUri = "content://contacts/1"
    )

    private lateinit var dataSource: SharedPrefDataSource

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences("test", Context.MODE_PRIVATE)
        dataSource = OneContactWidgetPrefDS(prefs)
    }

    @Test
    fun `saveWidget and getWidget`() {
        // When
        dataSource.saveWidget(widget)
        val result = dataSource.getWidget(1)

        // Then
        assertThat(result).isEqualTo(widget)
    }

    @Test
    fun `getWidget returns null if widget does not exist`() {
        // When
        val result = dataSource.getWidget(2)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `deleteWidget deletes widget data`() {
        // Given
        dataSource.saveWidget(widget)
        assertThat(dataSource.getWidget(1)).isNotNull()

        // When
        dataSource.deleteWidget(1)

        // Then
        assertThat(dataSource.getWidget(1)).isNull()
    }
}
