package com.blaxsoftware.directcallwidget.legacy

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.blaxsoftware.directcallwidget.DirectCallWidgetApp
import com.blaxsoftware.directcallwidget.appwidget.DirectCallWidgetProvider
import com.blaxsoftware.directcallwidget.appwidget.DirectCallWidgetProvider1x1
import com.blaxsoftware.directcallwidget.findByComponent
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.spyk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowApplication

@RunWith(AndroidJUnit4::class)
class LegacyWidgetsTest {

    private lateinit var context: Context
    private lateinit var contextShadow: ShadowApplication

    @MockK
    private lateinit var appWidgetManagerMock: AppWidgetManager

    private lateinit var legacyWidgets: LegacyWidgets

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        context = ApplicationProvider.getApplicationContext<DirectCallWidgetApp>()
        contextShadow = shadowOf(context as DirectCallWidgetApp?)
        contextShadow.clearBroadcastIntents()
        every { appWidgetManagerMock.getAppWidgetIds(any()) } returns testWidgetIds
        legacyWidgets = LegacyWidgets(context, appWidgetManagerMock)
    }

    @Test
    fun notifiesDirectCallWidgetProvider() {
        // Given
        val widgetProviderSpy = spyk(DirectCallWidgetProvider())

        // When
        legacyWidgets.updateAll()
        val intent = contextShadow
            .broadcastIntents
            .findByComponent<DirectCallWidgetProvider>()
        widgetProviderSpy.onReceive(context, intent)

        // Then
        verify { widgetProviderSpy.onUpdate(any(), any(), testWidgetIds) }
    }

    @Test
    fun notifiesDirectCallWidgetProvider1x1() {
        // Given
        val widgetProviderSpy = spyk(DirectCallWidgetProvider1x1())

        // When
        legacyWidgets.updateAll()
        val intent = contextShadow
            .broadcastIntents
            .findByComponent<DirectCallWidgetProvider1x1>()
        widgetProviderSpy.onReceive(context, intent)

        // Then
        verify { widgetProviderSpy.onUpdate(any(), any(), testWidgetIds) }
    }

    companion object {
        private val testWidgetIds = intArrayOf(1, 2, 3)
    }
}