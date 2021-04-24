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
import androidx.core.database.*
import dev.fpral.modelresolver.annotations.CursorColumn
import java.lang.Double
import java.lang.Float
import java.lang.Long
import java.lang.Short
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.*
import kotlin.reflect.jvm.javaType

inline fun <reified T : Any> Cursor.getAll(fromTheBeginning: Boolean = true): List<T> {
    val result = mutableListOf<T>()
    if (fromTheBeginning) moveToFirst()
    while (!isAfterLast) {
        result.add(read())
    }
    return result
}

inline fun <reified T : Any> Cursor.read(thenMoveToNext: Boolean = true): T {
    val cursor = this
    return T::class.createInstance().apply {
        javaClass.kotlin.memberProperties
                .filter { it.hasAnnotation<CursorColumn>() }
                .filterIsInstance(KMutableProperty1::class.java)
                .forEach { prop ->
                    val colName = prop.findAnnotation<CursorColumn>()!!.columnName
                    val colIndex = getColumnIndex(colName)
                    if (colIndex == -1) return@forEach
                    val propJavaClass = prop.returnType.javaType as Class<*>
                    val cursorGetter = CursorClass.getterByJavaClass(propJavaClass)
                    prop.setter.call(this, cursorGetter?.call(cursor, colIndex))
                }
        if (thenMoveToNext) moveToNext()
    }
}

inline fun <reified T : Any> Cursor.get(columnName: String, clazz: Class<T>? = null): T? {
    val colIndex = getColumnIndex(columnName)
    if (colIndex == -1) return null
    CursorClass.getter<T>()?.let {
        return it.call(this, colIndex) as T?
    }
    return null
}

object CursorClass {

    val GETTERS = mapOf(
            String::class.javaObjectType to Cursor::getStringOrNull,
            Integer::class.javaObjectType to Cursor::getIntOrNull,
            //Blob::class.java to Cursor::getBlobOrNull, TODO
            Double::class.javaObjectType to Cursor::getDoubleOrNull,
            Float::class.javaObjectType to Cursor::getFloatOrNull,
            Long::class.javaObjectType to Cursor::getLongOrNull,
            Short::class.javaObjectType to Cursor::getShortOrNull
    )

    inline fun <reified T : Any> getter() = GETTERS.entries.firstOrNull { entry ->
        T::class.java.isAssignableFrom(entry.key)
    }?.value

    fun <T : Any> getterByJavaClass(javaClass: Class<T>) = GETTERS.entries.firstOrNull { entry ->
        javaClass.isAssignableFrom(entry.key)
    }?.value
}