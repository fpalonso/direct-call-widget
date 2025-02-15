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
class SettingsFragment @Inject constructor() : PreferenceFragmentCompat() {

    interface Callback {
        fun onOssLicensesClicked()
    }

    @Inject
    lateinit var analytics: FirebaseAnalytics

    @Inject
    lateinit var preferences: Preferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference<ListPreference>(preferences.onTapKey)
            ?.setOnPreferenceChangeListener { _, newValue ->
                // Just log to Analytics
                val paramValue = when (newValue) {
                    preferences.dialValue -> Analytics.ParamValue.SETTING_ON_TAP_DIAL
                    else -> Analytics.ParamValue.SETTING_ON_TAP_CALL
                }
                val bundle = bundleOf(
                    FirebaseAnalytics.Param.VALUE to paramValue
                )
                analytics.logEvent(Analytics.Event.SETTING_ON_TAP_CHANGED, bundle)
                true
            }

        findPreference<Preference>(preferences.privacyPolicyKey)
            ?.setOnPreferenceClickListener {
                analytics.logEvent(Analytics.Event.SETTING_PRIVACY_POLICY_CLICK, null)
                val privacyPolicyIntent = Intent(Intent.ACTION_VIEW, PRIVACY_POLICY_URL.toUri())
                startActivity(privacyPolicyIntent)
                true
            }

        findPreference<Preference>(preferences.betaKey)
            ?.setOnPreferenceClickListener {
                analytics.logEvent(Analytics.Event.SETTING_BETA_CLICK, null)
                val joinBetaIntent = Intent(Intent.ACTION_VIEW, JOIN_BETA_URL.toUri())
                startActivity(joinBetaIntent)
                true
            }

        findPreference<Preference>(preferences.contributeKey)
            ?.setOnPreferenceClickListener {
                analytics.logEvent(Analytics.Event.SETTING_CONTRIBUTE_CLICK, null)
                val gitHubIntent = Intent(Intent.ACTION_VIEW, REPO_URL.toUri())
                startActivity(gitHubIntent)
                true
            }

        findPreference<Preference>(preferences.ossLicensesKey)
            ?.setOnPreferenceClickListener {
                analytics.logEvent(Analytics.Event.OSS_LICENSES_CLICK, null)
                (activity as? Callback)?.onOssLicensesClicked()
                true
            }

        findPreference<Preference>(preferences.versionKey)
            ?.title = activity
                ?.getString(
                    R.string.pref_version,
                    arguments?.getString(Arguments.VERSION_KEY)
                )
    }

    object Arguments {
        const val VERSION_KEY = "version"
    }

    private companion object {
        const val PRIVACY_POLICY_URL = "https://dcw.ferp.dev/privacy"
        const val JOIN_BETA_URL = "https://play.google.com/apps/testing/com.blaxsoftware.directcallwidget"
        const val REPO_URL = "https://github.com/fpalonso/direct-call-widget"
    }
}