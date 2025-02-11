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

/** Key prefixes used for accessing a widget data in SharedPreferences. */
object SharedPrefKeys {
    private const val DISPLAY_NAME_KEY_PREFIX = "name_"
    private const val PHONE_NUMBER_KEY_PREFIX = "phone_"
    private const val PHONE_TYPE_KEY_PREFIX = "phone_type_"
    private const val PICTURE_URI_KEY_PREFIX = "pic_"

    fun displayNameKey(appWidgetId: Int): String = "$DISPLAY_NAME_KEY_PREFIX$appWidgetId"

    fun phoneNumberKey(appWidgetId: Int): String = "$PHONE_NUMBER_KEY_PREFIX$appWidgetId"

    fun phoneTypeKey(appWidgetId: Int): String = "$PHONE_TYPE_KEY_PREFIX$appWidgetId"

    fun pictureUriKey(appWidgetId: Int): String = "$PICTURE_URI_KEY_PREFIX$appWidgetId"
}
