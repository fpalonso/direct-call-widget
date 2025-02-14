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

package dev.ferp.dcw.core.preferences

import android.content.SharedPreferences
import dev.ferp.dcw.core.preferences.di.UserPreferences
import javax.inject.Inject
import javax.inject.Singleton

/** Wrapper class for accessing user preferences */
@Singleton
class Preferences @Inject constructor(
    @UserPreferences private val sharedPrefs: SharedPreferences
) {
    /** Returns the action to perform when the widget is clicked. */
    fun getWidgetClickAction(): WidgetClickAction {
        val action = sharedPrefs.getString(KEY_ON_TAP, ON_TAP_CALL)
        return when (action) {
            ON_TAP_DIAL -> WidgetClickAction.DIAL
            else -> WidgetClickAction.CALL
        }
    }

    enum class WidgetClickAction {
        DIAL,
        CALL
    }

    companion object {
        // Keys
        const val KEY_ON_TAP: String = "pref_onTap"
        const val KEY_BETA: String = "pref_btester"
        const val KEY_CONTRIBUTE: String = "pref_contribute"

        // Values
        const val ON_TAP_DIAL = "0"
        const val ON_TAP_CALL = "1"
    }
}