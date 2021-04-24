/*
 * Direct Call Widget - The widget that makes contacts accessible
 * Copyright (C) 2021 Fer P. A.
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

@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package dev.fpral.modelresolver

import android.database.Cursor
import dev.fpral.modelresolver.annotations.CursorColumn
import java.lang.Double
import java.lang.Float
import java.lang.Long
import java.lang.Short
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.javaType

class CursorColumnResolver(private val cursor: Cursor) : PropertyValueResolver {

    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalArgumentException::class)
    override fun <T, V> resolvePropertyValue(property: KProperty1<T, V>): V? {
        checkAnnotation(property)
        val colName = property.findAnnotation<CursorColumn>()!!.columnName
        val propType = property.returnType.javaType as Class<*>
        return get(colName, propType)
    }

    override fun <T, V> resolvePropertyValueOrNull(property: KProperty1<T, V>): V? {
        return try {
            resolvePropertyValue(property)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    @Throws(IllegalArgumentException::class)
    private fun checkAnnotation(property: KProperty1<*, *>) {
        if (!property.hasAnnotation<CursorColumn>()) {
            throw IllegalArgumentException(
                    "Property expected to be annotated with ${CursorColumn::class.simpleName}")
        }
    }

    fun <V> get(columnName: String, jClass: Class<*>): V? {
        return get(cursor.getColumnIndex(columnName), jClass)
    }

    @Suppress("UNCHECKED_CAST")
    fun <V> get(columnIndex: Int, jClass: Class<*>): V? {
        if (columnIndex == -1) return null
        return when {
            jClass.isAssignableFrom(String::class.java) -> cursor.getString(columnIndex) as V?
            jClass.isAssignableFrom(Integer::class.java) -> cursor.getInt(columnIndex) as V?
            jClass.isAssignableFrom(Float::class.java) -> cursor.getFloat(columnIndex) as V?
            jClass.isAssignableFrom(Double::class.java) -> cursor.getDouble(columnIndex) as V?
            jClass.isAssignableFrom(Long::class.java) -> cursor.getLong(columnIndex) as V?
            jClass.isAssignableFrom(Short::class.java) -> cursor.getShort(columnIndex) as V?
            else -> null
        }
    }
}