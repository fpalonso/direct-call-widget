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

package com.blaxsoftware.directcallwidget.ui

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.dataStoreFile
import androidx.glance.state.GlanceStateDefinition
import com.blaxsoftware.directcallwidget.data.MultiContactInfo
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream
import java.io.OutputStream

object MultiContactStateDefinition : GlanceStateDefinition<MultiContactInfo> {

    private const val DATASTORE_FILENAME_PREFIX = "multiContactWidget_"

    override suspend fun getDataStore(
        context: Context,
        fileKey: String
    ): DataStore<MultiContactInfo> = DataStoreFactory.create(
        serializer = MultiContactSerializer,
        produceFile = { getLocation(context, fileKey) }
    )

    override fun getLocation(context: Context, fileKey: String): File =
        context.dataStoreFile(DATASTORE_FILENAME_PREFIX + fileKey)

    private object MultiContactSerializer : Serializer<MultiContactInfo> {

        override val defaultValue = MultiContactInfo.Unavailable("Widget info unavailable")

        override suspend fun readFrom(input: InputStream): MultiContactInfo = try {
            Json.decodeFromString(
                deserializer = MultiContactInfo.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            throw CorruptionException("Could not read widget info: ${e.message}")
        }

        override suspend fun writeTo(t: MultiContactInfo, output: OutputStream) {
            output.use {
                it.write(
                    Json.encodeToString(MultiContactInfo.serializer(), t).encodeToByteArray()
                )
            }
        }
    }
}
