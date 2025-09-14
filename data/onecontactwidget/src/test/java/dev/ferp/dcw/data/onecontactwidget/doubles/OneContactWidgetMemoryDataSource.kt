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

package dev.ferp.dcw.data.onecontactwidget.doubles

import dev.ferp.dcw.data.onecontactwidget.source.local.LocalOneContactWidget
import dev.ferp.dcw.data.onecontactwidget.source.OneContactWidgetDataSource

internal class OneContactWidgetMemoryDataSource : OneContactWidgetDataSource {
    private val widgets = mutableMapOf<Int, LocalOneContactWidget>()

    override fun addWidget(
        appWidgetId: Int,
        displayName: String?,
        phoneNumber: String,
        phoneType: Int?,
        pictureUri: String?
    ) {
        widgets[appWidgetId] = LocalOneContactWidget(
            appWidgetId = appWidgetId,
            displayName = displayName,
            phoneNumber = phoneNumber,
            phoneType = phoneType,
            pictureUri = pictureUri
        )
    }

    override fun getWidget(widgetId: Int): Result<LocalOneContactWidget> {
        return widgets[widgetId]?.let { widget ->
            Result.success(widget)
        } ?: Result.failure(IllegalArgumentException("Widget not found"))
    }

    override fun deleteWidget(widgetId: Int): Boolean {
        val result = widgets.contains(widgetId)
        widgets.remove(widgetId)
        return result
    }
}