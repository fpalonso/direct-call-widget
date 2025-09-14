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

/** Contract used for accessing a widget data stored in SharedPreferences. */
internal object PreferencesContract {
    internal const val FILE_NAME = "widget_data"

    private object KeyPrefixes {
        const val DISPLAY_NAME = "name"
        const val PHONE_NUMBER = "phone"
        const val PHONE_TYPE = "phone_type"
        const val PICTURE_URI = "pic"
    }

    fun displayNameKey(appWidgetId: Int): String = "${KeyPrefixes.DISPLAY_NAME}_$appWidgetId"

    fun phoneNumberKey(appWidgetId: Int): String = "${KeyPrefixes.PHONE_NUMBER}_$appWidgetId"

    fun phoneTypeKey(appWidgetId: Int): String = "${KeyPrefixes.PHONE_TYPE}_$appWidgetId"

    fun pictureUriKey(appWidgetId: Int): String = "${KeyPrefixes.PICTURE_URI}_$appWidgetId"
}
