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

import android.content.SharedPreferences
import android.provider.ContactsContract.CommonDataKinds
import androidx.core.content.edit
import dev.ferp.dcw.data.onecontactwidget.source.sharedprefs.SharedPrefKeys.displayNameKey
import dev.ferp.dcw.data.onecontactwidget.source.sharedprefs.SharedPrefKeys.phoneNumberKey
import dev.ferp.dcw.data.onecontactwidget.source.sharedprefs.SharedPrefKeys.phoneTypeKey
import dev.ferp.dcw.data.onecontactwidget.source.sharedprefs.SharedPrefKeys.pictureUriKey
import dev.ferp.dcw.data.onecontactwidget.source.sharedprefs.di.OneContactWidgetData
import javax.inject.Inject

class OneContactWidgetPrefDS @Inject constructor(
    @OneContactWidgetData private val prefs: SharedPreferences
) : SharedPrefDataSource {

    override fun saveWidget(widget: SharedPrefWidget) {
        prefs.edit {
            putString(displayNameKey(widget.appWidgetId), widget.displayName)
            putString(phoneNumberKey(widget.appWidgetId), widget.phoneNumber)
            putInt(
                phoneTypeKey(widget.appWidgetId),
                widget.phoneType ?: CommonDataKinds.Phone.TYPE_MAIN
            )
            putString(pictureUriKey(widget.appWidgetId), widget.pictureUri)
        }
    }

    override fun getWidget(widgetId: Int): SharedPrefWidget? {
        return if (prefs.contains(phoneNumberKey(widgetId))) {
            SharedPrefWidget(
                appWidgetId = widgetId,
                displayName = prefs.getString(displayNameKey(widgetId), null),
                phoneNumber = prefs.getString(phoneNumberKey(widgetId), "") ?: "",
                phoneType = prefs.getInt(phoneTypeKey(widgetId), CommonDataKinds.Phone.TYPE_MAIN),
                pictureUri = prefs.getString(pictureUriKey(widgetId), null)
            )
        } else null
    }

    override fun deleteWidget(widgetId: Int): Boolean {
        val result = prefs.contains(phoneNumberKey(widgetId))
        prefs.edit {
            remove(displayNameKey(widgetId))
            remove(phoneNumberKey(widgetId))
            remove(phoneTypeKey(widgetId))
            remove(pictureUriKey(widgetId))
        }
        return result
    }
}
