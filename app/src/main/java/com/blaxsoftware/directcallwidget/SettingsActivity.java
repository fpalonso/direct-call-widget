/*
 * Direct Call Widget - The widget that makes contacts accessible
 * Copyright (C) 2020 Fer P. A.
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

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import dev.ferp.dcw.feature.settings.SettingsFragment;

@AndroidEntryPoint
public class SettingsActivity extends AppCompatActivity {

    @Inject
    FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Settings");
        params.putString(FirebaseAnalytics.Param.SCREEN_CLASS, SettingsActivity.class.getName());
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params);
        setContentView(R.layout.activity_settings);
        setSupportActionBar(findViewById(R.id.topAppBar));

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragContainer, new SettingsFragment())
                .commit();
    }
}
