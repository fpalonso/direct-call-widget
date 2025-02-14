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
package dev.ferp.dcw.feature.settings

import android.content.Intent
import android.os.Bundle
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.AndroidEntryPoint
import dev.ferp.dcw.core.analytics.Analytics
import dev.ferp.dcw.core.preferences.Preferences
import javax.inject.Inject

// TODO add tests for this class - Hilt won't make it easy
@AndroidEntryPoint
class SettingsFragment @Inject constructor(
    private val analytics: FirebaseAnalytics
) : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference<ListPreference>(Preferences.KEY_ON_TAP)
            ?.setOnPreferenceChangeListener { _, newValue ->
                // Just log to Analytics
                val paramValue = when (newValue) {
                    Preferences.ON_TAP_DIAL -> Analytics.ParamValue.SETTING_ON_TAP_DIAL
                    else -> Analytics.ParamValue.SETTING_ON_TAP_CALL
                }
                val bundle = bundleOf(
                    FirebaseAnalytics.Param.VALUE to paramValue
                )
                analytics.logEvent(Analytics.Event.SETTING_ON_TAP_CHANGED, bundle)
                true
            }

        findPreference<Preference>(Preferences.KEY_BETA)
            ?.setOnPreferenceClickListener {
                analytics.logEvent(Analytics.Event.SETTING_BETA_CLICK, null)
                val joinBetaIntent = Intent(Intent.ACTION_VIEW, JOIN_BETA_URL.toUri())
                startActivity(joinBetaIntent)
                true
            }

        findPreference<Preference>(Preferences.KEY_CONTRIBUTE)
            ?.setOnPreferenceClickListener {
                analytics.logEvent(Analytics.Event.SETTING_CONTRIBUTE_CLICK, null)
                val gitHubIntent = Intent(Intent.ACTION_VIEW, REPO_URL.toUri())
                startActivity(gitHubIntent)
                true
            }
    }

    private companion object {
        const val JOIN_BETA_URL = "https://play.google.com/apps/testing/com.blaxsoftware.directcallwidget"
        const val REPO_URL = "https://github.com/fpalonso/direct-call-widget"
    }
}