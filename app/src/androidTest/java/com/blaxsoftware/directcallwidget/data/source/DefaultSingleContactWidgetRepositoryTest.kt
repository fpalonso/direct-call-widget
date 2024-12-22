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

package com.blaxsoftware.directcallwidget.data.source

import com.blaxsoftware.directcallwidget.SharedPreferencesRule
import com.google.common.truth.Truth
import org.junit.Rule
import org.junit.Test

class DefaultSingleContactWidgetRepositoryTest {

    @get:Rule
    val sharedPreferencesRule = SharedPreferencesRule("testPrefs.txt")

    private val dataSource: SingleContactWidgetRepository by lazy {
        DefaultSingleContactWidgetRepository(sharedPreferencesRule.sharedPreferences)
    }

    @Test
    fun writeAndReadWidgetDataFromSharedPreferences() {
        with(dataSource) {
            insertWidget(fakeWidgetData)
            getWidgetById(fakeWidgetData.widgetId).also {
                Truth.assertThat(it).isEqualTo(fakeWidgetData)
            }
        }
    }

    @Test
    fun writeAndDeleteWidgetDataFromSharedPreferences() {
        with(dataSource) {
            insertWidget(fakeWidgetData)
            deleteWidgetById(fakeWidgetData.widgetId)
            getWidgetById(fakeWidgetData.widgetId).also {
                Truth.assertThat(it).isNull()
            }
        }
    }
}
