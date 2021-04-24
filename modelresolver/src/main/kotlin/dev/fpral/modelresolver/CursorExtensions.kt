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

@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN", "SpellCheckingInspection")

package dev.fpral.modelresolver

import android.database.Cursor
import dev.fpral.modelresolver.annotations.CursorColumn
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

inline fun <reified T : Any> Cursor.getAll(fromTheBeginning: Boolean = true): List<T> {
    return getAll(T::class, fromTheBeginning)
}

fun <T : Any> Cursor.getAll(klass: KClass<T>, fromTheBeginning: Boolean = true): List<T> {
    val result = mutableListOf<T>()
    if (fromTheBeginning) moveToFirst()
    while (!isAfterLast) {
        result.add(read(klass))
    }
    return result
}

inline fun <reified T : Any> Cursor.read(thenMoveToNext: Boolean = true): T {
    return read(T::class, thenMoveToNext)
}

fun <T : Any> Cursor.read(kClass: KClass<T>, thenMoveToNext: Boolean = true): T {
    return read(kClass, CursorColumnResolver(this), thenMoveToNext)
}

fun <T : Any> Cursor.read(
        kClass: KClass<T>,
        resolver: PropertyValueResolver,
        thenMoveToNext: Boolean = true
): T {
    return kClass.createInstance().apply {
        val instance: T = this
        javaClass.kotlin.memberProperties
                .filter { it.hasAnnotation<CursorColumn>() }
                .filterIsInstance(KMutableProperty1::class.java)
                .forEach { prop ->
                    prop.isAccessible = true
                    prop.setter.call(instance, resolver.resolvePropertyValueOrNull(prop))
                }
        if (thenMoveToNext) moveToNext()
    }
}

inline fun <reified T : Any> Cursor.get(columnName: String): T? {
    return CursorColumnResolver(this).get(columnName, T::class.java)
}