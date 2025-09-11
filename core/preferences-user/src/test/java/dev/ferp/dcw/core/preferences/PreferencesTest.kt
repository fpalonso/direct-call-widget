package dev.ferp.dcw.core.preferences

import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PreferencesTest {

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var preferences: Preferences

    @Before
    fun init() {
        sharedPrefs = mockk()
        preferences = Preferences(ApplicationProvider.getApplicationContext(), sharedPrefs)
    }

    @Test
    fun `getWidgetClickAction returns DIAL when the preference is set to 0`() {
        // Given
        every {
            sharedPrefs.getString(preferences.onTapKey, any())
        } returns preferences.dialValue

        // When
        val result = preferences.getWidgetClickAction()

        // Then
        assertThat(result).isEqualTo(Preferences.WidgetClickAction.DIAL)
    }

    @Test
    fun `getWidgetClickAction returns CALL when the preference is set to 1`() {
        // Given
        every {
            sharedPrefs.getString(preferences.onTapKey, any())
        } returns preferences.callValue

        // When
        val result = preferences.getWidgetClickAction()

        // Then
        assertThat(result).isEqualTo(Preferences.WidgetClickAction.CALL)
    }

    @Test
    fun `getWidgetClickAction returns CALL when the preference is set to an unknown value`() {
        // Given
        every {
            sharedPrefs.getString(preferences.onTapKey, any())
        } returns null

        // When
        val result = preferences.getWidgetClickAction()

        // Then
        assertThat(result).isEqualTo(Preferences.WidgetClickAction.CALL)
    }
}