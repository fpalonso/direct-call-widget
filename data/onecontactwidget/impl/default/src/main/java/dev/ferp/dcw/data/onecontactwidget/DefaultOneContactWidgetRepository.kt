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

import dev.ferp.dcw.data.contacts.OneContactWidget
import dev.ferp.dcw.data.contacts.OneContactWidgetRepository
import dev.ferp.dcw.data.onecontactwidget.di.SharedPrefDS
import dev.ferp.dcw.data.onecontactwidget.source.sharedprefs.SharedPrefDataSource
import javax.inject.Inject

class DefaultOneContactWidgetRepository @Inject constructor(
    @SharedPrefDS private val sharedPrefsDataSource: SharedPrefDataSource
) : OneContactWidgetRepository {

    override suspend fun createWidget(
        appWidgetId: Int,
        displayName: String,
        phoneNumber: String,
        phoneType: Int,
        pictureUri: String
    ) {
        val widget = OneContactWidget(
            appWidgetId = appWidgetId,
            displayName = displayName,
            phoneNumber = phoneNumber,
            phoneType = phoneType,
            pictureUri = pictureUri
        ).toSharedPrefsWidget()
        sharedPrefsDataSource.saveWidget(widget)
    }

    override suspend fun getWidget(appWidgetId: Int): OneContactWidget? {
        return sharedPrefsDataSource.getWidget(appWidgetId)?.toExternal()
    }

    override suspend fun deleteWidget(appWidgetId: Int): Boolean {
        return sharedPrefsDataSource.deleteWidget(appWidgetId)
    }
}
