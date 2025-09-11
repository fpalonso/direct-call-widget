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

import android.content.SharedPreferences
import android.provider.ContactsContract
import androidx.core.content.edit
import dev.ferp.dcw.data.onecontactwidget.source.local.PreferencesContract.displayNameKey
import dev.ferp.dcw.data.onecontactwidget.source.local.PreferencesContract.phoneNumberKey
import dev.ferp.dcw.data.onecontactwidget.source.local.PreferencesContract.phoneTypeKey
import dev.ferp.dcw.data.onecontactwidget.source.local.PreferencesContract.pictureUriKey
import dev.ferp.dcw.data.onecontactwidget.di.OneContactWidget
import dev.ferp.dcw.data.onecontactwidget.source.OneContactWidgetDataSource
import javax.inject.Inject

internal class OneContactWidgetPreferencesDataSource @Inject constructor(
    @OneContactWidget private val preferences: SharedPreferences
) : OneContactWidgetDataSource {

    override fun addWidget(
        appWidgetId: Int,
        displayName: String?,
        phoneNumber: String,
        phoneType: Int?,
        pictureUri: String?
    ) {
        preferences.edit {
            putString(displayNameKey(appWidgetId), displayName)
            putString(phoneNumberKey(appWidgetId), phoneNumber)
            putInt(
                phoneTypeKey(appWidgetId),
                phoneType ?: ContactsContract.CommonDataKinds.Phone.TYPE_MAIN
            )
            putString(pictureUriKey(appWidgetId), pictureUri)
        }
    }

    override fun getWidget(widgetId: Int): Result<LocalOneContactWidget> {
        return if (preferences.contains(phoneNumberKey(widgetId))) {
            Result.success(
                LocalOneContactWidget(
                    appWidgetId = widgetId,
                    displayName = preferences.getString(displayNameKey(widgetId), null),
                    phoneNumber = preferences.getString(phoneNumberKey(widgetId), "").orEmpty(),
                    phoneType = preferences.getInt(
                        phoneTypeKey(widgetId),
                        ContactsContract.CommonDataKinds.Phone.TYPE_MAIN
                    ),
                    pictureUri = preferences.getString(pictureUriKey(widgetId), null)
                )
            )
        } else {
            Result.failure(IllegalArgumentException("Widget $widgetId not found"))
        }
    }

    override fun deleteWidget(widgetId: Int): Boolean {
        val result = preferences.contains(phoneNumberKey(widgetId))
        preferences.edit {
            remove(displayNameKey(widgetId))
            remove(phoneNumberKey(widgetId))
            remove(phoneTypeKey(widgetId))
            remove(pictureUriKey(widgetId))
        }
        return result
    }
}