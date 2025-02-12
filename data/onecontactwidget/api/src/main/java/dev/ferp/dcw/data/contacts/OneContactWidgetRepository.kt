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

package dev.ferp.dcw.data.contacts

/** Repository for widgets information */
interface OneContactWidgetRepository {

    /**
     * Adds the given widget information to the repository.
     *
     * @param phoneType On Android, this field is the value as it comes from the contacts
     * repository. For example: CommonDataKinds.Phone.TYPE_HOME
     */
    suspend fun createWidget(
        appWidgetId: Int,
        displayName: String,
        phoneNumber: String,
        phoneType: Int,
        pictureUri: String
    )

    /** Returns the information of the given widget id. */
    suspend fun getWidget(appWidgetId: Int): OneContactWidget?

    /**
     * Deletes the information of the given widget id.
     * @return whether the widget existed and could be deleted
     */
    suspend fun deleteWidget(appWidgetId: Int): Boolean

    /** Deletes the information of the given widget ids. */
    suspend fun deleteWidgets(appWidgetIds: IntArray)
}
