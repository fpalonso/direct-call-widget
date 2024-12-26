/*
 * Direct Call Widget - The widget that makes contacts accessible
 * Copyright (C) 2020 Fer P. A.
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
import android.provider.ContactsContract.CommonDataKinds.Phone
import androidx.core.content.edit
import com.blaxsoftware.directcallwidget.data.SingleContactWidget
import com.blaxsoftware.directcallwidget.di.LegacyWidgetInfo
import javax.inject.Inject

/**
 * Data source class that retrieves widget data from the given SharedPreferences object.
 */
class DefaultSingleContactWidgetRepository @Inject constructor(
    @LegacyWidgetInfo private val preferences: SharedPreferences
) : SingleContactWidgetRepository {

    override fun getWidgetById(widgetId: Int): SingleContactWidget? = with(preferences) {
        if (contains("$ATTR_DISPLAY_NAME$widgetId")
                || contains("$ATTR_PHONE_NUMBER$widgetId")
                || contains("$ATTR_PHONE_TYPE$widgetId")
                || contains("$ATTR_PICTURE_URI$widgetId")) {
            SingleContactWidget(
                    widgetId = widgetId,
                    displayName = getString("$ATTR_DISPLAY_NAME$widgetId", null),
                    phoneNumber = getString("$ATTR_PHONE_NUMBER$widgetId", "") ?: "",
                    phoneType = getInt("$ATTR_PHONE_TYPE$widgetId", Phone.TYPE_HOME),
                    pictureUri = getString("$ATTR_PICTURE_URI$widgetId", null)
            )
        } else null
    }

    override fun insertWidget(widget: SingleContactWidget) = with(widget) {
        preferences.edit {
            putString("$ATTR_DISPLAY_NAME$widgetId", displayName)
            putString("$ATTR_PHONE_NUMBER$widgetId", phoneNumber)
            putInt("$ATTR_PHONE_TYPE$widgetId", phoneType)
            putString("$ATTR_PICTURE_URI$widgetId", pictureUri)
        }
    }

    override fun deleteWidgetById(widgetId: Int) {
        preferences.edit {
            remove("$ATTR_DISPLAY_NAME$widgetId")
            remove("$ATTR_PHONE_NUMBER$widgetId")
            remove("$ATTR_PHONE_TYPE$widgetId")
            remove("$ATTR_PICTURE_URI$widgetId")
        }
    }

    private companion object {
        private const val ATTR_DISPLAY_NAME = "name_"
        private const val ATTR_PHONE_NUMBER = "phone_"
        private const val ATTR_PHONE_TYPE = "phone_type_"
        private const val ATTR_PICTURE_URI = "pic_"
    }
}
