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

package dev.fpral.modelresolver.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import dev.fpral.modelresolver.annotations.CursorColumn
import dev.fpral.modelresolver.getAll
import dev.fpral.modelresolver.read
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

open class ModelRepository<T : Any>(
        private val klass: KClass<T>,
        private val contentUri: Uri,
        private val contentResolver: ContentResolver
) : Repository<T> {

    private val defaultProjection: Array<String> by lazy {
        klass.memberProperties
                .filter { it.hasAnnotation<CursorColumn>() }
                .map { it.findAnnotation<CursorColumn>()!!.columnName }
                .toTypedArray()
    }

    override fun getById(id: Any): T? {
        return query(buildUriForId(id), defaultProjection)
                ?.use { cursor -> cursor.read(klass) }
    }

    private fun buildUriForId(id: Any) = if (id is Long) {
        ContentUris.withAppendedId(contentUri, id)
    } else {
        Uri.withAppendedPath(contentUri, id.toString())
    }

    override fun getAll(): Collection<T> {
        return query(contentUri, defaultProjection)
                ?.use { cursor -> cursor.getAll(klass) } ?: emptyList()
    }

    private fun query(uri: Uri, projection: Array<String>): Cursor? {
        return contentResolver.query(uri, projection, null, null, null)
    }
}