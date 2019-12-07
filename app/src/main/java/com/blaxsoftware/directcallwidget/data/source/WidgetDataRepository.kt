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
import com.blaxsoftware.directcallwidget.data.WidgetData

class WidgetDataRepository(
        private val sharedPreferences: SharedPreferences
) {
    fun getByWidgetId(widgetId: Int): WidgetData {
        return WidgetData(
                displayName = sharedPreferences.getString(SHAREDPREF_WIDGET_DISPLAY_NAME + widgetId, null),
                phone = sharedPreferences.getString(SHAREDPREF_WIDGET_PHONE + widgetId, null),
                phoneType = sharedPreferences.getInt(SHAREDPREF_WIDGET_PHONE_TYPE + widgetId, 0),
                picUri = sharedPreferences.getString(SHAREDPREF_WIDGET_PHOTO_URL + widgetId, null)
        )
    }
}
