/*
 * Direct Call Widget - The widget that makes contacts accessible
 * Copyright (C) 2020 Fer P. A.
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

package com.blaxsoftware.directcallwidget

import android.content.Context
import com.blaxsoftware.directcallwidget.data.source.*
import java.io.File

interface ServiceLocator {
    val contactDataSource: ContactDataSource
    val widgetDataSource: WidgetDataSource
    val widgetPicDataSource: WidgetPicDataSource

    companion object {
        private val LOCK = Any()
        private lateinit var instance: DefaultServiceLocator

        fun getInstance(context: Context): ServiceLocator {
            synchronized(LOCK) {
                if (!::instance.isInitialized) {
                    instance = DefaultServiceLocator(context.applicationContext)
                }
                return instance
            }
        }
    }
}

class DefaultServiceLocator(private val context: Context) : ServiceLocator {

    override val contactDataSource: ContactDataSource by lazy {
        ContactRepository(context.contentResolver)
    }

    override val widgetDataSource: WidgetDataSource by lazy {
        WidgetDataRepository(context.getSharedPreferences(
                "widget_data",
                Context.MODE_PRIVATE
        ))
    }

    override val widgetPicDataSource: WidgetPicDataSource by lazy {
        WidgetPicRepository(
                context.contentResolver,
                picsDir = File(context.filesDir, "pics")
        )
    }
}

val Context.serviceLocator: ServiceLocator
    get() = ServiceLocator.getInstance(this)

val Context.contactRepository: ContactDataSource
    get() = serviceLocator.contactDataSource

val Context.widgetRepository: WidgetDataSource
    get() = serviceLocator.widgetDataSource

val Context.widgetPicRepository: WidgetPicDataSource
    get() = serviceLocator.widgetPicDataSource
