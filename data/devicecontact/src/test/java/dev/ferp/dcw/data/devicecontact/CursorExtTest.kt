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

package dev.ferp.dcw.data.devicecontact

import android.database.MatrixCursor
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CursorExtTest {

    private lateinit var cursor: MatrixCursor

    @Before
    fun init() {
        cursor = MatrixCursor(arrayOf("column1", "column2")).apply {
            addRow(arrayOf("value1", "1"))
            moveToFirst()
        }
    }

    @Test
    fun `getString returns column value`() {
        val value = cursor.getString("column1")
        assertEquals("value1", value)
    }

    @Test
    fun `getString returns null if column is not present`() {
        val value = cursor.getString("column3")
        assertNull(value)
    }

    @Test
    fun `getInt returns column value`() {
        val value = cursor.getInt("column2")
        assertEquals(1, value)
    }

    @Test
    fun `getInt returns null if column is not present`() {
        val value = cursor.getInt("column3")
        assertNull(value)
    }
}