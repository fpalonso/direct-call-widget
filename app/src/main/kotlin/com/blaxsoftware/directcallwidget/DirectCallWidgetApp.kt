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

package com.blaxsoftware.directcallwidget

import androidx.glance.appwidget.updateAll
import androidx.multidex.MultiDexApplication
import com.blaxsoftware.directcallwidget.ui.MultiContactAppWidget
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@HiltAndroidApp
class DirectCallWidgetApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        updateGlanceAppWidgets()
    }

    private fun updateGlanceAppWidgets() {
        // TODO use a CoroutineWorker instead
        GlobalScope.launch {
            MultiContactAppWidget().updateAll(this@DirectCallWidgetApp)
        }
    }
}