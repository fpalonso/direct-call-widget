/*
 * Direct Call Widget - The widget that makes contacts accessible
 * Copyright (C) 2019 Fer P. A.
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

import androidx.test.core.app.ApplicationProvider;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.blaxsoftware.directcallwidget.rules.ClearSharedPreferencesRule;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.not;

@LargeTest
public class SettingsActivityTest {

    @Rule
    public ActivityTestRule settingsActivityRule = new ActivityTestRule<>(SettingsActivity.class);

    @Rule
    public ClearSharedPreferencesRule clearSharedPreferencesRule =
            new ClearSharedPreferencesRule(ApplicationProvider.getApplicationContext());

    @Test
    public void onTapSettingIsDisplayedWithDefaultValue() {
        onView(allOf(not(withText(R.string.pref_onTap)), hasSibling(withText(R.string.pref_onTap))))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.pref_onTap_entry_dial)));
    }

    @Test
    public void onTapSettingOptions() {
        onView(withText(R.string.pref_onTap))
                .perform(click());

        onView(withText(R.string.pref_onTap_entry_call))
                .check(matches(isDisplayed()));

        onView(withText(R.string.pref_onTap_entry_dial))
                .check(matches(isDisplayed()));
    }

    @Test
    public void changeOnTapSetting() {
        onView(withText(R.string.pref_onTap))
                .perform(click());

        onView(withText(R.string.pref_onTap_entry_call))
                .perform(click());

        onView(allOf(not(withText(R.string.pref_onTap)), hasSibling(withText(R.string.pref_onTap))))
                .check(matches(withText(R.string.pref_onTap_entry_call)));

        onView(withText(R.string.pref_onTap))
                .perform(click());

        onView(withText(R.string.pref_onTap_entry_dial))
                .perform(click());

        onView(allOf(not(withText(R.string.pref_onTap)), hasSibling(withText(R.string.pref_onTap))))
                .check(matches(withText(R.string.pref_onTap_entry_dial)));
    }
}
