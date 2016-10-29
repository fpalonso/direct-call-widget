package com.blaxsoftware.directcallwidget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.blaxsoftware.directcallwidget.R;
import com.blaxsoftware.directcallwidget.appwidget.DirectCallWidgetProvider;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent updateWidget = new Intent(this, DirectCallWidgetProvider.class);
        updateWidget.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        sendBroadcast(updateWidget);

        getSupportActionBar().setTitle(R.string.settings);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            updatePreferenceSummaries(getPreferenceScreen());
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceManager().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
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
