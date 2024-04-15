/*
 * Direct Call Widget - The widget that makes contacts accessible
 * Copyright (C) 2024 Fer P. A.
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

package com.blaxsoftware.directcallwidget.data.source.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.blaxsoftware.directcallwidget.data.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Query("SELECT * FROM Contact WHERE id = :id")
    suspend fun getContact(id: Long): Contact?

    @Query("SELECT * FROM Contact WHERE widgetId = :widgetId ORDER BY id")
    fun getWidgetContacts(widgetId: Long): Flow<List<Contact>>

    @Upsert
    suspend fun upsertWidgetContact(contact: Contact)
}