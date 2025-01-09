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

package com.blaxsoftware.directcallwidget

import android.content.SharedPreferences
import com.blaxsoftware.directcallwidget.di.LegacyWidgetInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Preferences @Inject constructor(
    @LegacyWidgetInfo private val sharedPrefs: SharedPreferences
) {
    fun getWidgetClickAction(): WidgetClickAction {
        val action = sharedPrefs.getString(PREF_ON_TAP_KEY, PREF_ON_TAP_CALL_VALUE)
        return when (action) {
            PREF_ON_TAP_DIAL_VALUE -> WidgetClickAction.DIAL
            else -> WidgetClickAction.CALL
        }
    }

    enum class WidgetClickAction {
        DIAL,
        CALL
    }

    companion object {
        const val PREF_ON_TAP_KEY: String = "pref_onTap"
        const val PREF_ON_TAP_DIAL_VALUE: String = "0"
        const val PREF_ON_TAP_CALL_VALUE: String = "1"
    }
}