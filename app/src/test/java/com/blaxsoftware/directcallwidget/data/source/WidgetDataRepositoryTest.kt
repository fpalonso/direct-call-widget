/*
 * Direct Call Widget - The widget that makes contacts accessible
 * Copyright (C) 2019 Fer P. A.
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

package com.blaxsoftware.directcallwidget.data.source

import android.content.SharedPreferences
import com.blaxsoftware.directcallwidget.Constants.*
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class WidgetDataRepositoryTest {

    @Mock
    var mockSharedPreferences: SharedPreferences? = null

    @InjectMocks
    var widgetDataRepository: WidgetDataRepository? = null

    @Test
    fun getByWidgetId() {
        `when`(mockSharedPreferences?.getString(SHAREDPREF_WIDGET_DISPLAY_NAME + 7, null))
                .thenReturn("User")
        `when`(mockSharedPreferences?.getString(SHAREDPREF_WIDGET_PHONE + 7, null))
                .thenReturn("555-9292")
        `when`(mockSharedPreferences?.getInt(SHAREDPREF_WIDGET_PHONE_TYPE + 7, 0))
                .thenReturn(1)
        `when`(mockSharedPreferences?.getString(SHAREDPREF_WIDGET_PHOTO_URL + 7, null))
                .thenReturn("content://pics/7")
        val widgetData = widgetDataRepository?.getByWidgetId(7)
        assertEquals("User", widgetData?.displayName)
        assertEquals("555-9292", widgetData?.phone)
        assertEquals(1, widgetData?.phoneType)
        assertEquals("content://pics/7", widgetData?.picUri)
    }
}
