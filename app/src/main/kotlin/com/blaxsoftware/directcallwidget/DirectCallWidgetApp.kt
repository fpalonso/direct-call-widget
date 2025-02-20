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

import androidx.multidex.MultiDexApplication
import com.blaxsoftware.directcallwidget.glance.MultiContactWidgets
import com.blaxsoftware.directcallwidget.legacy.LegacyWidgets
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Inject

@HiltAndroidApp
class DirectCallWidgetApp : MultiDexApplication() {

    @Inject lateinit var legacyWidgets: LegacyWidgets
    @Inject lateinit var multiContactWidgets: MultiContactWidgets

    val appVersionName: String by lazy {
        StringBuilder(BuildConfig.VERSION_NAME).apply {
            if (BuildConfig.DEBUG) {
                append(" (DEBUG)")
            }
        }.toString()
    }

    override fun onCreate() {
        super.onCreate()
        legacyWidgets.updateAll()
        multiContactWidgets.updateAll()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
}