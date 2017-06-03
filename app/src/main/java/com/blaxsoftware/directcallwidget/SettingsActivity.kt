package com.blaxsoftware.directcallwidget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceGroup
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem

import com.blaxsoftware.directcallwidget.R
import com.blaxsoftware.directcallwidget.appwidget.DirectCallWidgetProvider

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val updateWidget = Intent(this, DirectCallWidgetProvider::class.java)
        updateWidget.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        sendBroadcast(updateWidget)

        supportActionBar!!.setTitle(R.string.settings)
        fragmentManager.beginTransaction()
                .replace(android.R.id.content, SettingsFragment())
                .commit()
    }

    class SettingsFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preferences)
            updatePreferenceSummaries(preferenceScreen)
        }

        override fun onResume() {
            super.onResume()
            preferenceScreen.sharedPreferences
                    .registerOnSharedPreferenceChangeListener(this)
        }

        override fun onPause() {
            super.onPause()
            preferenceManager.sharedPreferences
                    .unregisterOnSharedPreferenceChangeListener(this)
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
            updatePreferenceSummaries(preferenceScreen)
        }

        private fun updatePreferenceSummaries(rootPreference: Preference) {
            if (rootPreference is PreferenceGroup) {
                val pg = rootPreference
                for (i in 0..pg.preferenceCount - 1) {
                    updatePreferenceSummaries(pg.getPreference(i))
                }
            } else if (rootPreference is ListPreference) {
                val lp = rootPreference
                lp.summary = lp.entry
            }
        }
    }
}
