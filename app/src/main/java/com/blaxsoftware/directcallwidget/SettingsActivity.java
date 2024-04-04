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

package com.blaxsoftware.directcallwidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;

import com.blaxsoftware.directcallwidget.analytics.Analytics;
import com.blaxsoftware.directcallwidget.analytics.AnalyticsHelper;
import com.blaxsoftware.directcallwidget.appwidget.DirectCallWidgetProvider;
import com.google.firebase.analytics.FirebaseAnalytics;

public class SettingsActivity extends AppCompatActivity {

    private static final String KEY_ON_TAP = "pref_onTap";
    private static final String KEY_BETA = "pref_btester";
    private static final String KEY_CONTRIBUTE = "pref_contribute";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAnalytics mFirebaseAnalytics = new AnalyticsHelper(this).getFirebaseAnalytics();
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Settings");
        params.putString(FirebaseAnalytics.Param.SCREEN_CLASS, SettingsActivity.class.getName());
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params);
        setContentView(R.layout.activity_settings);
        setSupportActionBar(findViewById(R.id.topAppBar));

        Intent updateWidget = new Intent(this, DirectCallWidgetProvider.class);
        updateWidget.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        sendBroadcast(updateWidget);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragContainer, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        private FirebaseAnalytics mFirebaseAnalytics;

        @Override
        public void onAttach(@NonNull Context context) {
            super.onAttach(context);
            mFirebaseAnalytics = new AnalyticsHelper(context).getFirebaseAnalytics();
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
            updatePreferenceSummaries(getPreferenceScreen());
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            switch (preference.getKey()) {
                case KEY_ON_TAP:
                    mFirebaseAnalytics.logEvent(Analytics.Event.SETTING_ON_TAP_CLICK, null);
                    break;
                case KEY_BETA:
                    mFirebaseAnalytics.logEvent(Analytics.Event.SETTING_BETA_CLICK, null);
                    final Uri joinBetaUri = Uri.parse("https://play.google.com/apps/testing/com.blaxsoftware.directcallwidget");
                    final Intent joinBetaIntent = new Intent(Intent.ACTION_VIEW, joinBetaUri);
                    startActivity(joinBetaIntent);
                    break;
                case KEY_CONTRIBUTE:
                    mFirebaseAnalytics.logEvent(Analytics.Event.SETTING_CONTRIBUTE_CLICK, null);
                    final Uri gitHubUri = Uri.parse("https://github.com/fpalonso/direct-call-widget");
                    final Intent gitHubIntent = new Intent(Intent.ACTION_VIEW, gitHubUri);
                    startActivity(gitHubIntent);
                    break;
            }
            return super.onPreferenceTreeClick(preference);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceManager().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (KEY_ON_TAP.equals(key)) {
                String value = sharedPreferences.getString(KEY_ON_TAP, WidgetClickReceiver.PREF_ONTAP_CALL_VALUE);
                String paramValue;
                if (WidgetClickReceiver.PREF_ONTAP_CALL_VALUE.equals(value)) {
                    paramValue = Analytics.ParamValue.SETTING_ON_TAP_CALL;
                } else {
                    paramValue = Analytics.ParamValue.SETTING_ON_TAP_DIAL;
                }
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.VALUE, paramValue);
                mFirebaseAnalytics.logEvent(Analytics.Event.SETTING_ON_TAP_CHANGED, bundle);
            }
            updatePreferenceSummaries(getPreferenceScreen());
        }

        private void updatePreferenceSummaries(Preference rootPreference) {
            if (rootPreference instanceof PreferenceGroup) {
                PreferenceGroup pg = (PreferenceGroup) rootPreference;
                for (int i = 0; i < pg.getPreferenceCount(); ++i) {
                    updatePreferenceSummaries(pg.getPreference(i));
                }
            } else if (rootPreference instanceof ListPreference) {
                ListPreference lp = (ListPreference) rootPreference;
                lp.setSummary(lp.getEntry());
            }
        }
    }
}
