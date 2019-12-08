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

package com.blaxsoftware.directcallwidget.di

import android.content.Context
import android.content.SharedPreferences
import com.blaxsoftware.directcallwidget.Constants
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PreferencesModule {

    @Provides
    @Singleton
    fun provideWidgetDataPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(
                Constants.SHAREDPREF_WIDGET, Context.MODE_PRIVATE)
    }
}
