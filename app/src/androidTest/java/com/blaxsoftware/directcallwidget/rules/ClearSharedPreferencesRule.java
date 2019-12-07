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

package com.blaxsoftware.directcallwidget.rules;

import android.content.Context;
import android.preference.PreferenceManager;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class ClearSharedPreferencesRule implements TestRule {
    private Context context;

    public ClearSharedPreferencesRule(Context context) {
        this.context = context;
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                clearSharedPreferences();
                base.evaluate();

                // Clearing SharedPreferences at the beginning might not be enough. For example,
                // it is not guaranteed that this rule runs before an ActivityTestRule.
                // Therefore, it is important that we undo any changes done by the current test
                // prior to starting the next one.
                clearSharedPreferences();
            }

            private void clearSharedPreferences() {
                PreferenceManager.getDefaultSharedPreferences(context)
                        .edit()
                        .clear()
                        .commit();
            }
        };
    }
}
