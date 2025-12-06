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

package com.blaxsoftware.directcallwidget.legacy

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.blaxsoftware.directcallwidget.appwidget.DirectCallWidgetProvider
import com.blaxsoftware.directcallwidget.appwidget.DirectCallWidgetProvider1x1
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/** Utility class for updating one-contact widgets. */
class LegacyWidgets @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val appWidgetManager: AppWidgetManager
) {
    fun updateAll() = providerClasses.forEach { updateAll(it) }

    private fun <T : AppWidgetProvider> updateAll(providerClass: Class<T>) {
        val appWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(appContext, providerClass)
        )
        update(appWidgetIds)
    }

    fun update(appWidgetIds: IntArray) {
        providerClasses.forEach {
            update(it, appWidgetIds)
        }
    }

    private fun <T : AppWidgetProvider> update(
        providerClass: Class<T>,
        appWidgetIds: IntArray
    ) {
        val updateIntent = Intent(appContext, providerClass)
            .setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
            .putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
        appContext.sendBroadcast(updateIntent)
    }

    companion object {

        private val providerClasses = arrayOf(
            DirectCallWidgetProvider::class.java,
            DirectCallWidgetProvider1x1::class.java
        )
    }
}