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

package dev.ferp.dcw.data.onecontactwidget.source.sharedprefs

class FakeSharedPrefDataSource : SharedPrefDataSource {

    private val widgetMap: MutableMap<Int, SharedPrefWidget> = mutableMapOf()

    override fun saveWidget(widget: SharedPrefWidget) {
        widgetMap[widget.appWidgetId] = widget
    }

    override fun getWidget(widgetId: Int): SharedPrefWidget? {
        return widgetMap[widgetId]
    }

    override fun deleteWidget(widgetId: Int): Boolean {
        val result = widgetMap.containsKey(widgetId)
        widgetMap.remove(widgetId)
        return result
    }
}