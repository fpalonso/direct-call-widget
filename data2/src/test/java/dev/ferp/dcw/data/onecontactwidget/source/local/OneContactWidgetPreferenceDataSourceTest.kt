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

package dev.ferp.dcw.data.onecontactwidget.source.local

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.ferp.dcw.data.onecontactwidget.source.OneContactWidgetDataSource
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OneContactWidgetPreferenceDataSourceTest {

    private val widget = LocalOneContactWidget(
        appWidgetId = 1,
        displayName = "John Doe",
        phoneNumber = "123456789",
        phoneType = 1,
        pictureUri = "content://contacts/1"
    )

    private lateinit var dataSource: OneContactWidgetDataSource

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences("test", Context.MODE_PRIVATE)
        dataSource = OneContactWidgetPreferencesDataSource(prefs)
    }

    @Test
    fun `saveWidget and getWidget`() {
        // When
        dataSource.addWidget(
            widget.appWidgetId,
            widget.displayName,
            widget.phoneNumber,
            widget.phoneType,
            widget.pictureUri
        )
        val result = dataSource.getWidget(1)

        // Then
        assertThat(result).isEqualTo(Result.success(widget))
    }

    @Test
    fun `getWidget returns null if widget does not exist`() {
        // When
        val result = dataSource.getWidget(2)

        // Then
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `deleteWidget deletes widget data`() {
        // Given
        dataSource.addWidget(
            widget.appWidgetId,
            widget.displayName,
            widget.phoneNumber,
            widget.phoneType,
            widget.pictureUri
        )
        assertThat(dataSource.getWidget(1).isSuccess).isTrue()

        // When
        dataSource.deleteWidget(1)

        // Then
        assertThat(dataSource.getWidget(1).isFailure).isTrue()
    }
}