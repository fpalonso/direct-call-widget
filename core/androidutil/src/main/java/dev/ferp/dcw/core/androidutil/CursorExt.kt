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

package dev.ferp.dcw.core.androidutil

import android.database.Cursor

fun Cursor.getStringOrNull(columnName: String): String? {
    val colIndex = getColumnIndex(columnName)
    if (colIndex !in 0 until columnCount) return null
    return getString(colIndex)
}

fun Cursor.getIntOrNull(columnName: String): Int? {
    val colIndex = getColumnIndex(columnName)
    if (colIndex !in 0 until columnCount) return null
    return getInt(colIndex)
}
