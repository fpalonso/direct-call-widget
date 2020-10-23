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

package com.blaxsoftware.directcallwidget

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.withChild
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class SettingsActivityTest {

    @get:Rule
    val activityScenarioRule = activityScenarioRule<SettingsActivity>()

    @Test
    fun supportPreferenceClickStartsEmail() {
        Intents.init()
        intending(hasAction(Intent.ACTION_SENDTO))
                .respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, Intent()))

        onView(withChild(withText(R.string.pref_support_title)))
                .perform(click())

        intended(hasAction(Intent.ACTION_SENDTO))
        val subject = "Direct Call Widget ${BuildConfig.VERSION_NAME}"
        intended(hasData("mailto:blax.software@gmail.com?subject=$subject"))
        intended(hasExtra(Intent.EXTRA_SUBJECT, subject))
        Intents.release()
    }

    @Test
    fun joinBetaPreferenceClickSendsToProperLink() {
        Intents.init()
        intending(hasAction(Intent.ACTION_VIEW))
                .respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, Intent()))

        onView(withChild(withText(R.string.pref_become_btester_title)))
                .perform(click())

        intended(hasAction(Intent.ACTION_VIEW))
        intended(hasData("https://play.google.com/apps/testing/com.blaxsoftware.directcallwidget"))
        Intents.release()
    }

    @Test
    fun contributePreferenceClickOpensGitHub() {
        Intents.init()
        intending(hasAction(Intent.ACTION_VIEW))
                .respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, Intent()))

        onView(withChild(withText(R.string.pref_contribute_title)))
                .perform(click())

        intended(hasAction(Intent.ACTION_VIEW))
        intended(hasData("https://github.com/fpalonso/direct-call-widget"))
        Intents.release()
    }
}
