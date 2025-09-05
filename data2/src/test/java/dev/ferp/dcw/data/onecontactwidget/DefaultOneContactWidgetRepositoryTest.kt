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

package dev.ferp.dcw.data.onecontactwidget

import com.google.common.truth.Truth.assertThat
import dev.ferp.dcw.core.domain.data.onecontactwidget.OneContactWidgetRepository
import dev.ferp.dcw.data.onecontactwidget.doubles.MemoryOneContactWidgetDataSource
import dev.ferp.dcw.data.onecontactwidget.mother.OneContactWidgetMother
import dev.ferp.dcw.data.onecontactwidget.source.local.OneContactWidgetDataSource
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultOneContactWidgetRepositoryTest {

    private lateinit var fakeDataSource: OneContactWidgetDataSource
    private lateinit var widgetRepository: OneContactWidgetRepository

    @Before
    fun init() {
        fakeDataSource = MemoryOneContactWidgetDataSource()
        widgetRepository = DefaultOneContactWidgetRepository(fakeDataSource)
    }

    @Test
    fun `Add and get widget`() = runTest {
        // Given
        val widget = OneContactWidgetMother.widget

        // When
        widgetRepository.addWidget(
            widget.appWidgetId,
            widget.phoneNumber,
            widget.displayName,
            widget.phoneType,
            widget.pictureUri
        )
        val expected = widgetRepository.getWidget(widget.appWidgetId)

        // Then
        assertThat(expected).isEqualTo(Result.success(widget))
    }

    @Test
    fun `getWidget returns failure if widget is not present`() = runTest {
        val result = widgetRepository.getWidget(2)
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `Add and delete widget`() = runTest {
        // Given
        val widget = OneContactWidgetMother.widget
        val anotherWidget = OneContactWidgetMother.anotherWidget

        // When
        widgetRepository.addWidget(
            widget.appWidgetId,
            widget.phoneNumber,
            widget.displayName,
            widget.phoneType,
            widget.pictureUri
        )
        widgetRepository.addWidget(
            anotherWidget.appWidgetId,
            anotherWidget.phoneNumber,
            anotherWidget.displayName,
            anotherWidget.phoneType,
            anotherWidget.pictureUri
        )
        widgetRepository.deleteWidget(1)
        val deletedWidget = widgetRepository.getWidget(widget.appWidgetId)
        val presentWidget = widgetRepository.getWidget(anotherWidget.appWidgetId)

        // Then
        assertThat(deletedWidget.isFailure).isTrue()
        assertThat(presentWidget).isEqualTo(Result.success(anotherWidget))
    }

    @Test
    fun `Add and delete two widgets`() = runTest {
        // Given
        val widget = OneContactWidgetMother.widget
        val anotherWidget = OneContactWidgetMother.anotherWidget
        val thirdWidget = OneContactWidgetMother.thirdWidget

        // When
        widgetRepository.addWidget(
            widget.appWidgetId,
            widget.phoneNumber,
            widget.displayName,
            widget.phoneType,
            widget.pictureUri
        )
        widgetRepository.addWidget(
            anotherWidget.appWidgetId,
            anotherWidget.phoneNumber,
            anotherWidget.displayName,
            anotherWidget.phoneType,
            anotherWidget.pictureUri
        )
        widgetRepository.addWidget(
            thirdWidget.appWidgetId,
            thirdWidget.phoneNumber,
            thirdWidget.displayName,
            thirdWidget.phoneType,
            thirdWidget.pictureUri
        )
        widgetRepository.deleteWidgets(intArrayOf(1, 2))
        val deletedWidget = widgetRepository.getWidget(widget.appWidgetId)
        val anotherDeletedWidget = widgetRepository.getWidget(anotherWidget.appWidgetId)
        val presentWidget = widgetRepository.getWidget(thirdWidget.appWidgetId)

        // Then
        assertThat(deletedWidget.isFailure).isTrue()
        assertThat(anotherDeletedWidget.isFailure).isTrue()
        assertThat(presentWidget).isEqualTo(Result.success(thirdWidget))
    }
}