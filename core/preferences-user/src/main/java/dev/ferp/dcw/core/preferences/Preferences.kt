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

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.ferp.dcw.core.preferences.di.UserPreferences
import javax.inject.Inject
import javax.inject.Singleton

/** Wrapper class for accessing user preferences */
@Singleton
class Preferences @Inject constructor(
    @ApplicationContext private val context: Context,
    @UserPreferences private val sharedPrefs: SharedPreferences
) {
    val onTapKey: String by lazy {
        context.getString(R.string.pref_onTap_key)
    }

    val privacyPolicyKey: String by lazy {
        context.getString(R.string.pref_privacy_policy_key)
    }

    val betaKey: String by lazy {
        context.getString(R.string.pref_become_btester_key)
    }

    val contributeKey: String by lazy {
        context.getString(R.string.pref_contribute_key)
    }

    val ossLicensesKey: String by lazy {
        context.getString(R.string.pref_oss_licenses_key)
    }

    val versionKey: String by lazy {
        context.getString(R.string.pref_version_key)
    }

    val dialValue: String by lazy {
        context.getString(R.string.pref_onTap_dial_value)
    }

    val callValue: String by lazy {
        context.getString(R.string.pref_onTap_call_value)
    }

    /** Returns the action to perform when the widget is clicked. */
    fun getWidgetClickAction(): WidgetClickAction {
        val action = sharedPrefs.getString(onTapKey, callValue)
        return when (action) {
            dialValue -> WidgetClickAction.DIAL
            else -> WidgetClickAction.CALL
        }
    }

    enum class WidgetClickAction {
        DIAL,
        CALL
    }
}